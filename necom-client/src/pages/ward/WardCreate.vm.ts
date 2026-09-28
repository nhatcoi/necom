import { useState } from 'react';
import { useForm, zodResolver } from '@mantine/form';
import WardConfigs from 'pages/ward/WardConfigs';
import ProvinceConfigs from 'pages/province/ProvinceConfigs';
import { WardRequest, WardResponse } from 'models/Ward';
import { ProvinceResponse } from 'models/Province';
import { SelectOption } from 'types';
import useCreateApi from 'hooks/use-create-api';
import useGetAllApi from 'hooks/use-get-all-api';

function useWardCreateViewModel() {
  const form = useForm({
    initialValues: WardConfigs.initialCreateUpdateFormValues,
    schema: zodResolver(WardConfigs.createUpdateFormSchema),
  });

  const [provinceSelectList, setProvinceSelectList] = useState<SelectOption[]>([]);

  const createApi = useCreateApi<WardRequest, WardResponse>(WardConfigs.resourceUrl);
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

  const handleFormSubmit = form.onSubmit((formValues) => {
    const requestBody: WardRequest = {
      name: formValues.name,
      code: formValues.code,
      provinceId: Number(formValues.provinceId),
    };
    createApi.mutate(requestBody);
  });

  return {
    form,
    handleFormSubmit,
    provinceSelectList,
  };
}

export default useWardCreateViewModel;
