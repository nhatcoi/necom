import { useForm, zodResolver } from '@mantine/form';
import AddressConfigs from 'pages/address/AddressConfigs';
import { AddressRequest, AddressResponse } from 'models/Address';
import useCreateApi from 'hooks/use-create-api';
import useGetAllApi from 'hooks/use-get-all-api';
import { ProvinceResponse } from 'models/Province';
import ProvinceConfigs from 'pages/province/ProvinceConfigs';
import { WardResponse } from 'models/Ward';
import WardConfigs from 'pages/ward/WardConfigs';
import { useState } from 'react';
import { SelectOption } from 'types';
import useSelectAddress from 'hooks/use-select-address';

function useAddressCreateViewModel() {
  const form = useForm({
    initialValues: AddressConfigs.initialCreateUpdateFormValues,
    schema: zodResolver(AddressConfigs.createUpdateFormSchema),
  });

  useSelectAddress(form, 'provinceId', 'wardId');

  const [provinceSelectList, setProvinceSelectList] = useState<SelectOption[]>([]);
  const [wardSelectList, setWardSelectList] = useState<SelectOption[]>([]);

  const createApi = useCreateApi<AddressRequest, AddressResponse>(AddressConfigs.resourceUrl);
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
    const requestBody: AddressRequest = {
      line: formValues.line || null,
      provinceId: Number(formValues.provinceId) || null,
      districtId: null,
      wardId: Number(formValues.wardId) || null,
    };
    createApi.mutate(requestBody);
  });

  return {
    form,
    handleFormSubmit,
    provinceSelectList,
    wardSelectList,
  };
}

export default useAddressCreateViewModel;
