import { useState } from 'react';
import { useForm, zodResolver } from '@mantine/form';
import UserConfigs from 'pages/user/UserConfigs';
import { UserRequest, UserResponse } from 'models/User';
import useCreateApi from 'hooks/use-create-api';
import useGetAllApi from 'hooks/use-get-all-api';
import { ProvinceResponse } from 'models/Province';
import ProvinceConfigs from 'pages/province/ProvinceConfigs';
import { WardResponse } from 'models/Ward';
import WardConfigs from 'pages/ward/WardConfigs';
import { RoleResponse } from 'models/Role';
import RoleConfigs from 'pages/role/RoleConfigs';
import { SelectOption } from 'types';
import useSelectAddress from 'hooks/use-select-address';

function useUserCreateViewModel() {
  const form = useForm({
    initialValues: UserConfigs.initialCreateUpdateFormValues,
    schema: zodResolver(UserConfigs.createUpdateFormSchema),
  });

  useSelectAddress(form, 'address.provinceId', 'address.wardId');

  const [provinceSelectList, setProvinceSelectList] = useState<SelectOption[]>([]);
  const [wardSelectList, setWardSelectList] = useState<SelectOption[]>([]);
  const [roleSelectList, setRoleSelectList] = useState<SelectOption[]>([]);

  const createApi = useCreateApi<UserRequest, UserResponse>(UserConfigs.resourceUrl);
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
  useGetAllApi<RoleResponse>(RoleConfigs.resourceUrl, RoleConfigs.resourceKey,
    { sort: 'id,asc', all: 1 },
    (roleListResponse) => {
      const selectList: SelectOption[] = roleListResponse.content.map((item) => ({
        value: String(item.id),
        label: item.name,
      }));
      setRoleSelectList(selectList);
    }
  );

  const handleFormSubmit = form.onSubmit((formValues) => {
    const requestBody: UserRequest = {
      username: formValues.username,
      password: formValues.password,
      fullname: formValues.fullname,
      email: formValues.email,
      phone: formValues.phone,
      gender: formValues.gender,
      address: {
        line: formValues['address.line'],
        provinceId: Number(formValues['address.provinceId']),
        districtId: null,
        wardId: formValues['address.wardId'] ? Number(formValues['address.wardId']) : null,
      },
      avatar: formValues.avatar.trim() || null,
      status: Number(formValues.status),
      roles: formValues.roles.map((roleId) => ({ id: Number(roleId) })),
    };
    createApi.mutate(requestBody);
  });

  const genderSelectList: SelectOption[] = [
    {
      value: 'M',
      label: 'Nam',
    },
    {
      value: 'F',
      label: 'Nữ',
    },
  ];

  const statusSelectList: SelectOption[] = [
    {
      value: '1',
      label: 'Đã kích hoạt',
    },
    {
      value: '2',
      label: 'Chưa kích hoạt',
    },
  ];

  return {
    form,
    handleFormSubmit,
    genderSelectList,
    provinceSelectList,
    wardSelectList,
    statusSelectList,
    roleSelectList,
  };
}

export default useUserCreateViewModel;
