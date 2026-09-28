import { useForm, zodResolver } from '@mantine/form';
import SupplierConfigs from 'pages/supplier/SupplierConfigs';
import { SupplierRequest, SupplierResponse } from 'models/Supplier';
import useCreateApi from 'hooks/use-create-api';
import { useState } from 'react';
import { SelectOption } from 'types';
import useGetAllApi from 'hooks/use-get-all-api';
import { ProvinceResponse } from 'models/Province';
import ProvinceConfigs from 'pages/province/ProvinceConfigs';
import { WardResponse } from 'models/Ward';
import WardConfigs from 'pages/ward/WardConfigs';
import { AddressRequest } from 'models/Address';
import useSelectAddress from 'hooks/use-select-address';

function useSupplierCreateViewModel() {
  const form = useForm({
    initialValues: SupplierConfigs.initialCreateUpdateFormValues,
    schema: zodResolver(SupplierConfigs.createUpdateFormSchema),
  });

  useSelectAddress(form, 'address.provinceId', 'address.wardId');

  const [provinceSelectList, setProvinceSelectList] = useState<SelectOption[]>([]);
  const [wardSelectList, setWardSelectList] = useState<SelectOption[]>([]);

  const createApi = useCreateApi<SupplierRequest, SupplierResponse>(SupplierConfigs.resourceUrl);
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
    const addressRequest: AddressRequest = {
      line: formValues['address.line'] || null,
      provinceId: Number(formValues['address.provinceId']) || null,
      districtId: null,
      wardId: formValues['address.wardId'] ? Number(formValues['address.wardId']) : null,
    };
    const requestBody: SupplierRequest = {
      displayName: formValues.displayName,
      code: formValues.code,
      contactFullname: formValues.contactFullname || null,
      contactEmail: formValues.contactEmail || null,
      contactPhone: formValues.contactPhone || null,
      companyName: formValues.companyName || null,
      taxCode: formValues.taxCode || null,
      email: formValues.email || null,
      phone: formValues.phone || null,
      fax: formValues.fax || null,
      website: formValues.website || null,
      address: Object.values(addressRequest).every(value => value === null) ? null : addressRequest,
      description: formValues.description || null,
      note: formValues.note || null,
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

export default useSupplierCreateViewModel;
