import BaseResponse from 'models/BaseResponse';
import { DistrictResponse } from 'models/District';
import { ProvinceResponse } from 'models/Province';

export interface WardResponse extends BaseResponse {
  name: string;
  code: string;
  province?: ProvinceResponse;
  district?: DistrictResponse;
}

export interface WardRequest {
  name: string;
  code: string;
  provinceId: number;
  districtId?: number | null;
}

