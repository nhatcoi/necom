FROM python:3.12-slim

WORKDIR /app
RUN pip install --no-cache-dir onnxruntime==1.20.1 sentencepiece==0.2.0 numpy==2.2.1 fastapi==0.115.6 uvicorn==0.34.0 huggingface_hub==0.27.0

# Tải model khi build để container khởi động không cần mạng
RUN python -c "from huggingface_hub import hf_hub_download as d; \
[d('Xenova/multilingual-e5-small', f, local_dir='/model') for f in ['onnx/model_quantized.onnx', 'sentencepiece.bpe.model']]" \
 && mv /model/onnx/model_quantized.onnx /model/model_quantized.onnx && rm -rf /model/onnx /root/.cache

COPY app.py .
ENV MODEL_DIR=/model THREADS=1 MALLOC_ARENA_MAX=2
EXPOSE 80
CMD ["uvicorn", "app:app", "--host", "0.0.0.0", "--port", "80", "--workers", "1"]
