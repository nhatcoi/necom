import { useForm, zodResolver } from '@mantine/form';
import DestinationConfigs from 'pages/destination/DestinationConfigs';
import { DestinationRequest, DestinationResponse } from 'models/Destination';
import useCreateApi from 'hooks/use-create-api';
import { useState } from 'react';
import { SelectOption } from 'types';
import useGetAllApi from 'hooks/use-get-all-api';
import { ProvinceResponse } from 'models/Province';
import ProvinceConfigs from 'pages/province/ProvinceConfigs';
import { WardResponse } from 'models/Ward';
import WardConfigs from 'pages/ward/WardConfigs';
import useSelectAddress from 'hooks/use-select-address';

function useDestinationCreateViewModel() {
  const form = useForm({
    initialValues: DestinationConfigs.initialCreateUpdateFormValues,
    schema: zodResolver(DestinationConfigs.createUpdateFormSchema),
  });

  useSelectAddress(form, 'address.provinceId', 'address.wardId');

  const [provinceSelectList, setProvinceSelectList] = useState<SelectOption[]>([]);
  const [wardSelectList, setWardSelectList] = useState<SelectOption[]>([]);

  const createApi = useCreateApi<DestinationRequest, DestinationResponse>(DestinationConfigs.resourceUrl);
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
    const requestBody: DestinationRequest = {
      contactFullname: formValues.contactFullname || null,
      contactEmail: formValues.contactEmail || null,
      contactPhone: formValues.contactPhone || null,
      address: {
        line: formValues['address.line'],
        provinceId: Number(formValues['address.provinceId']),
        districtId: null,
        wardId: Number(formValues['address.wardId']),
      },
      status: Number(formValues.status),
    };
    createApi.mutate(requestBody);
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
    form,
    handleFormSubmit,
    provinceSelectList,
    wardSelectList,
    statusSelectList,
  };
}

export default useDestinationCreateViewModel;
