import { useState } from 'react';
import { useForm, zodResolver } from '@mantine/form';
import AddressConfigs from 'pages/address/AddressConfigs';
import { AddressRequest, AddressResponse } from 'models/Address';
import useUpdateApi from 'hooks/use-update-api';
import useGetByIdApi from 'hooks/use-get-by-id-api';
import MiscUtils from 'utils/MiscUtils';
import useGetAllApi from 'hooks/use-get-all-api';
import { ProvinceResponse } from 'models/Province';
import ProvinceConfigs from 'pages/province/ProvinceConfigs';
import { WardResponse } from 'models/Ward';
import WardConfigs from 'pages/ward/WardConfigs';
import { SelectOption } from 'types';
import useSelectAddress from 'hooks/use-select-address';

function useAddressUpdateViewModel(id: number) {
  const form = useForm({
    initialValues: AddressConfigs.initialCreateUpdateFormValues,
    schema: zodResolver(AddressConfigs.createUpdateFormSchema),
  });

  useSelectAddress(form, 'provinceId', 'wardId');

  const [address, setAddress] = useState<AddressResponse>();
  const [prevFormValues, setPrevFormValues] = useState<typeof form.values>();
  const [provinceSelectList, setProvinceSelectList] = useState<SelectOption[]>([]);
  const [wardSelectList, setWardSelectList] = useState<SelectOption[]>([]);

  const updateApi = useUpdateApi<AddressRequest, AddressResponse>(AddressConfigs.resourceUrl, AddressConfigs.resourceKey, id);
  useGetByIdApi<AddressResponse>(AddressConfigs.resourceUrl, AddressConfigs.resourceKey, id,
    (addressResponse) => {
      setAddress(addressResponse);
      const formValues: typeof form.values = {
        line: addressResponse.line || '',
        provinceId: addressResponse.province ? String(addressResponse.province.id) : null,
        wardId: addressResponse.ward ? String(addressResponse.ward.id) : null,
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
    { all: 1, filter: `province.id==${form.values.provinceId || 0}` },
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
      const requestBody: AddressRequest = {
        line: formValues.line || null,
        provinceId: Number(formValues.provinceId) || null,
        districtId: null,
        wardId: Number(formValues.wardId) || null,
      };
      updateApi.mutate(requestBody);
    }
  });

  return {
    address,
    form,
    handleFormSubmit,
    provinceSelectList,
    wardSelectList,
  };
}

export default useAddressUpdateViewModel;
