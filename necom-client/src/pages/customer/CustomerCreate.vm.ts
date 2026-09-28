import { useForm, zodResolver } from '@mantine/form';
import CustomerConfigs from 'pages/customer/CustomerConfigs';
import { CustomerRequest, CustomerResponse } from 'models/Customer';
import useCreateApi from 'hooks/use-create-api';
import useGetAllApi from 'hooks/use-get-all-api';
import { ProvinceResponse } from 'models/Province';
import ProvinceConfigs from 'pages/province/ProvinceConfigs';
import { WardResponse } from 'models/Ward';
import WardConfigs from 'pages/ward/WardConfigs';
import { useState } from 'react';
import { SelectOption } from 'types';
import CustomerGroupConfigs from 'pages/customer-group/CustomerGroupConfigs';
import { CustomerGroupResponse } from 'models/CustomerGroup';
import CustomerStatusConfigs from 'pages/customer-status/CustomerStatusConfigs';
import { CustomerStatusResponse } from 'models/CustomerStatus';
import { CustomerResourceResponse } from 'models/CustomerResource';
import CustomerResourceConfigs from 'pages/customer-resource/CustomerResourceConfigs';
import useSelectAddress from 'hooks/use-select-address';

function useCustomerCreateViewModel() {
  const form = useForm({
    initialValues: CustomerConfigs.initialCreateUpdateFormValues,
    schema: zodResolver(CustomerConfigs.createUpdateFormSchema),
  });

  useSelectAddress(form, 'user.address.provinceId', 'user.address.wardId');

  const [provinceSelectList, setProvinceSelectList] = useState<SelectOption[]>([]);
  const [wardSelectList, setWardSelectList] = useState<SelectOption[]>([]);
  const [customerGroupSelectList, setCustomerGroupSelectList] = useState<SelectOption[]>([]);
  const [customerStatusSelectList, setCustomerStatusSelectList] = useState<SelectOption[]>([]);
  const [customerResourceSelectList, setCustomerResourceSelectList] = useState<SelectOption[]>([]);

  const createApi = useCreateApi<CustomerRequest, CustomerResponse>(CustomerConfigs.resourceUrl);
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
    { all: 1, filter: `province.id==${form.values['user.address.provinceId'] || 0}` },
    (wardListResponse) => {
      const selectList: SelectOption[] = wardListResponse.content.map((item) => ({
        value: String(item.id),
        label: item.name,
      }));
      setWardSelectList(selectList);
    }
  );
  useGetAllApi<CustomerGroupResponse>(CustomerGroupConfigs.resourceUrl, CustomerGroupConfigs.resourceKey,
    { all: 1 },
    (customerGroupListResponse) => {
      const selectList: SelectOption[] = customerGroupListResponse.content.map((item) => ({
        value: String(item.id),
        label: item.name,
      }));
      setCustomerGroupSelectList(selectList);
    }
  );
  useGetAllApi<CustomerStatusResponse>(CustomerStatusConfigs.resourceUrl, CustomerStatusConfigs.resourceKey,
    { all: 1 },
    (customerStatusListResponse) => {
      const selectList: SelectOption[] = customerStatusListResponse.content.map((item) => ({
        value: String(item.id),
        label: item.name,
      }));
      setCustomerStatusSelectList(selectList);
    }
  );
  useGetAllApi<CustomerResourceResponse>(CustomerResourceConfigs.resourceUrl, CustomerResourceConfigs.resourceKey,
    { all: 1 },
    (customerResourceListResponse) => {
      const selectList: SelectOption[] = customerResourceListResponse.content.map((item) => ({
        value: String(item.id),
        label: item.name,
      }));
      setCustomerResourceSelectList(selectList);
    }
  );

  const handleFormSubmit = form.onSubmit((formValues) => {
    const requestBody: CustomerRequest = {
      user: {
        username: formValues['user.username'],
        password: formValues['user.password'],
        fullname: formValues['user.fullname'],
        email: formValues['user.email'],
        phone: formValues['user.phone'],
        gender: formValues['user.gender'],
        address: {
          line: formValues['user.address.line'],
          provinceId: Number(formValues['user.address.provinceId']),
          districtId: null,
          wardId: formValues['user.address.wardId'] ? Number(formValues['user.address.wardId']) : null,
        },
        avatar: formValues['user.avatar'].trim() || null,
        status: Number(formValues['user.status']),
        roles: [{ id: CustomerConfigs.CUSTOMER_ROLE_ID }],
      },
      customerGroupId: Number(formValues.customerGroupId),
      customerStatusId: Number(formValues.customerStatusId),
      customerResourceId: Number(formValues.customerResourceId),
    };
    createApi.mutate(requestBody);
  });

  const userGenderSelectList: SelectOption[] = [
    {
      value: 'M',
      label: 'Nam',
    },
    {
      value: 'F',
      label: 'Nữ',
    },
  ];

  const userStatusSelectList: SelectOption[] = [
    {
      value: '1',
      label: 'Đã kích hoạt',
    },
    {
      value: '2',
      label: 'Chưa kích hoạt',
    },
  ];

  const userRoleSelectList: SelectOption[] = [
    {
      value: String(CustomerConfigs.CUSTOMER_ROLE_ID),
      label: 'Khách hàng',
    },
  ];

  return {
    form,
    handleFormSubmit,
    userGenderSelectList,
    provinceSelectList,
    wardSelectList,
    userStatusSelectList,
    userRoleSelectList,
    customerGroupSelectList,
    customerStatusSelectList,
    customerResourceSelectList,
  };
}

export default useCustomerCreateViewModel;
