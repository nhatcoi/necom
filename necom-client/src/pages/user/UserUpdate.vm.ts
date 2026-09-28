import { useState } from 'react';
import { useForm, zodResolver } from '@mantine/form';
import UserConfigs from 'pages/user/UserConfigs';
import { UserRequest, UserResponse } from 'models/User';
import useUpdateApi from 'hooks/use-update-api';
import useGetByIdApi from 'hooks/use-get-by-id-api';
import MiscUtils from 'utils/MiscUtils';
import useGetAllApi from 'hooks/use-get-all-api';
import { ProvinceResponse } from 'models/Province';
import ProvinceConfigs from 'pages/province/ProvinceConfigs';
import { WardResponse } from 'models/Ward';
import WardConfigs from 'pages/ward/WardConfigs';
import { RoleResponse } from 'models/Role';
import { SelectOption } from 'types';
import RoleConfigs from 'pages/role/RoleConfigs';
import useAdminAuthStore from 'stores/use-admin-auth-store';
import useSelectAddress from 'hooks/use-select-address';

function useUserUpdateViewModel(id: number) {
  const form = useForm({
    initialValues: UserConfigs.initialCreateUpdateFormValues,
    schema: zodResolver(UserConfigs.createUpdateFormSchema),
  });

  useSelectAddress(form, 'address.provinceId', 'address.wardId');

  const { user: adminUser, updateUser: updateAdminUser } = useAdminAuthStore();

  const [user, setUser] = useState<UserResponse>();
  const [prevFormValues, setPrevFormValues] = useState<typeof form.values>();
  const [provinceSelectList, setProvinceSelectList] = useState<SelectOption[]>([]);
  const [wardSelectList, setWardSelectList] = useState<SelectOption[]>([]);
  const [roleSelectList, setRoleSelectList] = useState<SelectOption[]>([]);

  const updateApi = useUpdateApi<UserRequest, UserResponse>(UserConfigs.resourceUrl, UserConfigs.resourceKey, id);
  useGetByIdApi<UserResponse>(UserConfigs.resourceUrl, UserConfigs.resourceKey, id,
    (userResponse) => {
      setUser(userResponse);
      const formValues: typeof form.values = {
        username: userResponse.username,
        password: '',
        fullname: userResponse.fullname,
        email: userResponse.email,
        phone: userResponse.phone,
        gender: userResponse.gender,
        'address.line': userResponse.address.line || '',
        'address.provinceId': userResponse.address.province ? String(userResponse.address.province.id) : null,
        'address.wardId': userResponse.address.ward ? String(userResponse.address.ward.id) : null,
        avatar: userResponse.avatar || '',
        status: String(userResponse.status),
        roles: userResponse.roles.map((role) => String(role.id)),
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
    setPrevFormValues(formValues);
    if (!MiscUtils.isEquals(formValues, prevFormValues)) {
      if (user) {
        const requestBody: UserRequest = {
          username: formValues.username,
          password: formValues.password || null,
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
        updateApi.mutate(requestBody, {
          onSuccess: (userResponse) => {
            if (adminUser && formValues.username === adminUser.username) {
              updateAdminUser(userResponse);
            }
          },
        });
      }
    }
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

  const isDisabledUpdateButton = MiscUtils.isEquals(form.values, prevFormValues);

  return {
    user,
    form,
    handleFormSubmit,
    genderSelectList,
    provinceSelectList,
    wardSelectList,
    statusSelectList,
    roleSelectList,
    isDisabledUpdateButton,
  };
}

export default useUserUpdateViewModel;
