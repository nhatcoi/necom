import { useState } from 'react';
import { useForm, zodResolver } from '@mantine/form';
import WardConfigs from 'pages/ward/WardConfigs';
import ProvinceConfigs from 'pages/province/ProvinceConfigs';
import { WardRequest, WardResponse } from 'models/Ward';
import { ProvinceResponse } from 'models/Province';
import { SelectOption } from 'types';
import useUpdateApi from 'hooks/use-update-api';
import useGetByIdApi from 'hooks/use-get-by-id-api';
import useGetAllApi from 'hooks/use-get-all-api';
import MiscUtils from 'utils/MiscUtils';

function useWardUpdateViewModel(id: number) {
  const form = useForm({
    initialValues: WardConfigs.initialCreateUpdateFormValues,
    schema: zodResolver(WardConfigs.createUpdateFormSchema),
  });

  const [ward, setWard] = useState<WardResponse>();
  const [prevFormValues, setPrevFormValues] = useState<typeof form.values>();
  const [provinceSelectList, setProvinceSelectList] = useState<SelectOption[]>([]);

  const updateApi = useUpdateApi<WardRequest, WardResponse>(WardConfigs.resourceUrl, WardConfigs.resourceKey, id);
  useGetByIdApi<WardResponse>(WardConfigs.resourceUrl, WardConfigs.resourceKey, id,
    (wardResponse) => {
      setWard(wardResponse);
      const formValues: typeof form.values = {
        name: wardResponse.name,
        code: wardResponse.code,
        provinceId: wardResponse.province ? String(wardResponse.province.id) : null,
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

  const handleFormSubmit = form.onSubmit((formValues) => {
    setPrevFormValues(formValues);
    if (!MiscUtils.isEquals(formValues, prevFormValues)) {
      const requestBody: WardRequest = {
        name: formValues.name,
        code: formValues.code,
        provinceId: Number(formValues.provinceId),
      };
      updateApi.mutate(requestBody);
    }
  });

  return {
    ward,
    form,
    handleFormSubmit,
    provinceSelectList,
  };
}

export default useWardUpdateViewModel;
