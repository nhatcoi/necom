import { useState } from 'react';
import { useForm, zodResolver } from '@mantine/form';
import DestinationConfigs from 'pages/destination/DestinationConfigs';
import { DestinationRequest, DestinationResponse } from 'models/Destination';
import useUpdateApi from 'hooks/use-update-api';
import useGetByIdApi from 'hooks/use-get-by-id-api';
import MiscUtils from 'utils/MiscUtils';
import { SelectOption } from 'types';
import useGetAllApi from 'hooks/use-get-all-api';
import { ProvinceResponse } from 'models/Province';
import ProvinceConfigs from 'pages/province/ProvinceConfigs';
import { WardResponse } from 'models/Ward';
import WardConfigs from 'pages/ward/WardConfigs';
import useSelectAddress from 'hooks/use-select-address';

function useDestinationUpdateViewModel(id: number) {
  const form = useForm({
    initialValues: DestinationConfigs.initialCreateUpdateFormValues,
    schema: zodResolver(DestinationConfigs.createUpdateFormSchema),
  });

  useSelectAddress(form, 'address.provinceId', 'address.wardId');

  const [destination, setDestination] = useState<DestinationResponse>();
  const [prevFormValues, setPrevFormValues] = useState<typeof form.values>();
  const [provinceSelectList, setProvinceSelectList] = useState<SelectOption[]>([]);
  const [wardSelectList, setWardSelectList] = useState<SelectOption[]>([]);

  const updateApi = useUpdateApi<DestinationRequest, DestinationResponse>(DestinationConfigs.resourceUrl, DestinationConfigs.resourceKey, id);
  useGetByIdApi<DestinationResponse>(DestinationConfigs.resourceUrl, DestinationConfigs.resourceKey, id,
    (destinationResponse) => {
      setDestination(destinationResponse);
      const formValues: typeof form.values = {
        contactFullname: destinationResponse.contactFullname || '',
        contactEmail: destinationResponse.contactEmail || '',
        contactPhone: destinationResponse.contactPhone || '',
        'address.line': destinationResponse.address.line || '',
        'address.provinceId': destinationResponse.address.province ? String(destinationResponse.address.province.id) : null,
        'address.wardId': destinationResponse.address.ward ? String(destinationResponse.address.ward.id) : null,
        status: String(destinationResponse.status),
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
    destination,
    form,
    handleFormSubmit,
    provinceSelectList,
    wardSelectList,
    statusSelectList,
  };
}

export default useDestinationUpdateViewModel;
