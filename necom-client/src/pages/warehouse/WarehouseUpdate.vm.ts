import { useState } from 'react';
import { useForm, zodResolver } from '@mantine/form';
import WarehouseConfigs from 'pages/warehouse/WarehouseConfigs';
import { WarehouseRequest, WarehouseResponse } from 'models/Warehouse';
import useUpdateApi from 'hooks/use-update-api';
import useGetByIdApi from 'hooks/use-get-by-id-api';
import MiscUtils from 'utils/MiscUtils';
import { SelectOption } from 'types';
import useGetAllApi from 'hooks/use-get-all-api';
import { ProvinceResponse } from 'models/Province';
import ProvinceConfigs from 'pages/province/ProvinceConfigs';
import { WardResponse } from 'models/Ward';
import WardConfigs from 'pages/ward/WardConfigs';
import { AddressRequest } from 'models/Address';
import useSelectAddress from 'hooks/use-select-address';

function useWarehouseUpdateViewModel(id: number) {
  const form = useForm({
    initialValues: WarehouseConfigs.initialCreateUpdateFormValues,
    schema: zodResolver(WarehouseConfigs.createUpdateFormSchema),
  });

  useSelectAddress(form, 'address.provinceId', 'address.wardId');

  const [warehouse, setWarehouse] = useState<WarehouseResponse>();
  const [prevFormValues, setPrevFormValues] = useState<typeof form.values>();
  const [provinceSelectList, setProvinceSelectList] = useState<SelectOption[]>([]);
  const [wardSelectList, setWardSelectList] = useState<SelectOption[]>([]);

  const updateApi = useUpdateApi<WarehouseRequest, WarehouseResponse>(WarehouseConfigs.resourceUrl, WarehouseConfigs.resourceKey, id);
  useGetByIdApi<WarehouseResponse>(WarehouseConfigs.resourceUrl, WarehouseConfigs.resourceKey, id,
    (warehouseResponse) => {
      setWarehouse(warehouseResponse);
      const formValues: typeof form.values = {
        code: warehouseResponse.code,
        name: warehouseResponse.name,
        'address.line': warehouseResponse.address?.line || '',
        'address.provinceId': warehouseResponse.address?.province ? String(warehouseResponse.address.province.id) : null,
        'address.wardId': warehouseResponse.address?.ward ? String(warehouseResponse.address.ward.id) : null,
        status: String(warehouseResponse.status),
      };
      form.setValues(formValues);
      setPrevFormValues(formValues);
    }
  );
  useGetAllApi<ProvinceResponse>(ProvinceConfigs.resourceUrl, ProvinceConfigs.resourceKey,
    { all: 1 },
    (provinceListResponse) => {
      const selectList: SelectOption[] = provinceListResponse.content.map((item) => ({
        value: String(item.id),
        label: item.name,
      }));
      setProvinceSelectList(selectList);
    }
  );
  useGetAllApi<WardResponse>(WardConfigs.resourceUrl, WardConfigs.resourceKey,
    { all: 1, filter: `province.id==${form.values['address.provinceId'] || 0}` },
    (wardListResponse) => {
      const selectList: SelectOption[] = wardListResponse.content.map((item) => ({
        value: String(item.id),
        label: item.name,
      }));
      setWardSelectList(selectList);
    }
  );

  const handleFormSubmit = form.onSubmit((formValues) => {
    setPrevFormValues(formValues);
    if (!MiscUtils.isEquals(formValues, prevFormValues)) {
      const addressRequest: AddressRequest = {
        line: formValues['address.line'] || null,
        provinceId: Number(formValues['address.provinceId']) || null,
        districtId: null,
        wardId: formValues['address.wardId'] ? Number(formValues['address.wardId']) : null,
      };
      const requestBody: WarehouseRequest = {
        code: formValues.code,
        name: formValues.name,
        address: Object.values(addressRequest).every(value => value === null) ? null : addressRequest,
        status: Number(formValues.status),
      };
      updateApi.mutate(requestBody);
    }
  });

  const statusSelectList: SelectOption[] = [
    {
      value: '1',
      label: 'Có hiệu lực',
    },
    {
      value: '2',
      label: 'Vô hiệu lực',
    },
  ];

  return {
    warehouse,
    form,
    handleFormSubmit,
    provinceSelectList,
    wardSelectList,
    statusSelectList,
  };
}

export default useWarehouseUpdateViewModel;
