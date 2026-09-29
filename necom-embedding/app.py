"""
Service embedding nhẹ cho Necom: multilingual-e5-small bản ONNX lượng tử hóa int8 (~118MB, chạy CPU).
API tương thích text-embeddings-inference: POST /embed {"inputs": [...], "normalize": true} -> [[float, ...], ...]
"""
import os

import numpy as np
import onnxruntime as ort
from fastapi import FastAPI
from pydantic import BaseModel
import sentencepiece as spm

MODEL_DIR = os.environ.get('MODEL_DIR', '/model')
MAX_LENGTH = int(os.environ.get('MAX_LENGTH', '512'))
MAX_BATCH = int(os.environ.get('MAX_BATCH', '8'))

options = ort.SessionOptions()
options.intra_op_num_threads = int(os.environ.get('THREADS', '1'))
options.inter_op_num_threads = 1
options.enable_cpu_mem_arena = False  # giảm RAM giữ lại sau mỗi batch
# Không constant-folding: nếu gộp DequantizeLinear, bảng embedding int8 (250k từ vựng) bị bung thành float32 (~370MB)
options.graph_optimization_level = ort.GraphOptimizationLevel.ORT_DISABLE_ALL
session = ort.InferenceSession(os.path.join(MODEL_DIR, 'model_quantized.onnx'), options, providers=['CPUExecutionProvider'])
input_names = {i.name for i in session.get_inputs()}

# sentencepiece (~75MB RAM) thay cho tokenizer.json của HF (~250MB); cho kết quả giống hệt theo quy ước XLM-R
sp = spm.SentencePieceProcessor(model_file=os.path.join(MODEL_DIR, 'sentencepiece.bpe.model'))
BOS, PAD, EOS, UNK = 0, 1, 2, 3


def tokenize(text: str) -> list[int]:
    ids = [UNK if piece == 0 else piece + 1 for piece in sp.encode(text)]  # fairseq offset +1, <unk> = 3
    return [BOS] + ids[:MAX_LENGTH - 2] + [EOS]

app = FastAPI()


class EmbedRequest(BaseModel):
    inputs: list[str] | str
    normalize: bool = True
    truncate: bool = True


def embed(texts: list[str], normalize: bool) -> np.ndarray:
    tokenized = [tokenize(t) for t in texts]
    width = max(len(t) for t in tokenized)
    ids = np.full((len(tokenized), width), PAD, dtype=np.int64)
    mask = np.zeros((len(tokenized), width), dtype=np.int64)
    for row, tokens in enumerate(tokenized):
        ids[row, :len(tokens)] = tokens
        mask[row, :len(tokens)] = 1
    feeds = {'input_ids': ids, 'attention_mask': mask}
    if 'token_type_ids' in input_names:
        feeds['token_type_ids'] = np.zeros_like(ids)
    hidden = session.run(None, feeds)[0]  # (batch, tokens, dim)
    # e5 dùng mean pooling theo attention mask
    weights = mask[..., None].astype(np.float32)
    pooled = (hidden * weights).sum(axis=1) / np.clip(weights.sum(axis=1), 1e-9, None)
    if normalize:
        pooled = pooled / np.clip(np.linalg.norm(pooled, axis=1, keepdims=True), 1e-12, None)
    return pooled


@app.post('/embed')
def embed_endpoint(request: EmbedRequest):
    texts = [request.inputs] if isinstance(request.inputs, str) else request.inputs
    vectors = []
    for i in range(0, len(texts), MAX_BATCH):
        vectors.extend(embed(texts[i:i + MAX_BATCH], request.normalize).tolist())
    return vectors


@app.get('/health')
def health():
    return {'status': 'ok'}
