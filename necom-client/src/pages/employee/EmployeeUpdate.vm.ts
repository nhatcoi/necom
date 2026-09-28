import { useState } from 'react';
import { useForm, zodResolver } from '@mantine/form';
import EmployeeConfigs from 'pages/employee/EmployeeConfigs';
import { EmployeeRequest, EmployeeResponse } from 'models/Employee';
import useUpdateApi from 'hooks/use-update-api';
import useGetByIdApi from 'hooks/use-get-by-id-api';
import MiscUtils from 'utils/MiscUtils';
import useGetAllApi from 'hooks/use-get-all-api';
import { ProvinceResponse } from 'models/Province';
import ProvinceConfigs from 'pages/province/ProvinceConfigs';
import { WardResponse } from 'models/Ward';
import WardConfigs from 'pages/ward/WardConfigs';
import { SelectOption } from 'types';
import { OfficeResponse } from 'models/Office';
import OfficeConfigs from 'pages/office/OfficeConfigs';
import { DepartmentResponse } from 'models/Department';
import DepartmentConfigs from 'pages/department/DepartmentConfigs';
import { JobTypeResponse } from 'models/JobType';
import JobTypeConfigs from 'pages/job-type/JobTypeConfigs';
import { JobLevelResponse } from 'models/JobLevel';
import JobLevelConfigs from 'pages/job-level/JobLevelConfigs';
import { JobTitleResponse } from 'models/JobTitle';
import JobTitleConfigs from 'pages/job-title/JobTitleConfigs';
import useSelectAddress from 'hooks/use-select-address';

function useEmployeeUpdateViewModel(id: number) {
  const form = useForm({
    initialValues: EmployeeConfigs.initialCreateUpdateFormValues,
    schema: zodResolver(EmployeeConfigs.createUpdateFormSchema),
  });

  useSelectAddress(form, 'user.address.provinceId', 'user.address.wardId');

  const [employee, setEmployee] = useState<EmployeeResponse>();
  const [prevFormValues, setPrevFormValues] = useState<typeof form.values>();
  const [provinceSelectList, setProvinceSelectList] = useState<SelectOption[]>([]);
  const [wardSelectList, setWardSelectList] = useState<SelectOption[]>([]);
  const [officeSelectList, setOfficeSelectList] = useState<SelectOption[]>([]);
  const [departmentSelectList, setDepartmentSelectList] = useState<SelectOption[]>([]);
  const [jobTypeSelectList, setJobTypeSelectList] = useState<SelectOption[]>([]);
  const [jobLevelSelectList, setJobLevelSelectList] = useState<SelectOption[]>([]);
  const [jobTitleSelectList, setJobTitleSelectList] = useState<SelectOption[]>([]);

  const updateApi = useUpdateApi<EmployeeRequest, EmployeeResponse>(EmployeeConfigs.resourceUrl, EmployeeConfigs.resourceKey, id);
  useGetByIdApi<EmployeeResponse>(EmployeeConfigs.resourceUrl, EmployeeConfigs.resourceKey, id,
    (employeeResponse) => {
      setEmployee(employeeResponse);
      const formValues: typeof form.values = {
        'user.username': employeeResponse.user.username,
        'user.password': '',
        'user.fullname': employeeResponse.user.fullname,
        'user.email': employeeResponse.user.email,
        'user.phone': employeeResponse.user.phone,
        'user.gender': employeeResponse.user.gender,
        'user.address.line': employeeResponse.user.address.line || '',
        'user.address.provinceId': employeeResponse.user.address.province ? String(employeeResponse.user.address.province.id) : null,
        'user.address.wardId': employeeResponse.user.address.ward ? String(employeeResponse.user.address.ward.id) : null,
        'user.avatar': employeeResponse.user.avatar || '',
        'user.status': String(employeeResponse.user.status),
        'user.roles': [String(EmployeeConfigs.EMPLOYEE_ROLE_ID)],
        officeId: String(employeeResponse.office.id),
        departmentId: String(employeeResponse.department.id),
        jobTypeId: String(employeeResponse.jobType.id),
        jobLevelId: String(employeeResponse.jobLevel.id),
        jobTitleId: String(employeeResponse.jobTitle.id),
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
    { all: 1, filter: `province.id==${form.values['user.address.provinceId'] || 0}` },
    (wardListResponse) => {
      const selectList: SelectOption[] = wardListResponse.content.map((item) => ({
        value: String(item.id),
        label: item.name,
      }));
      setWardSelectList(selectList);
    }
  );
  useGetAllApi<OfficeResponse>(OfficeConfigs.resourceUrl, OfficeConfigs.resourceKey,
    { all: 1 },
    (officeListResponse) => {
      const selectList: SelectOption[] = officeListResponse.content.map((item) => ({
        value: String(item.id),
        label: item.name,
      }));
      setOfficeSelectList(selectList);
    }
  );
  useGetAllApi<DepartmentResponse>(DepartmentConfigs.resourceUrl, DepartmentConfigs.resourceKey,
    { all: 1 },
    (departmentListResponse) => {
      const selectList: SelectOption[] = departmentListResponse.content.map((item) => ({
        value: String(item.id),
        label: item.name,
      }));
      setDepartmentSelectList(selectList);
    }
  );
  useGetAllApi<JobTypeResponse>(JobTypeConfigs.resourceUrl, JobTypeConfigs.resourceKey,
    { all: 1 },
    (jobTypeListResponse) => {
      const selectList: SelectOption[] = jobTypeListResponse.content.map((item) => ({
        value: String(item.id),
        label: item.name,
      }));
      setJobTypeSelectList(selectList);
    }
  );
  useGetAllApi<JobLevelResponse>(JobLevelConfigs.resourceUrl, JobLevelConfigs.resourceKey,
    { all: 1 },
    (jobLevelListResponse) => {
      const selectList: SelectOption[] = jobLevelListResponse.content.map((item) => ({
        value: String(item.id),
        label: item.name,
      }));
      setJobLevelSelectList(selectList);
    }
  );
  useGetAllApi<JobTitleResponse>(JobTitleConfigs.resourceUrl, JobTitleConfigs.resourceKey,
    { all: 1 },
    (jobTitleListResponse) => {
      const selectList: SelectOption[] = jobTitleListResponse.content.map((item) => ({
        value: String(item.id),
        label: item.name,
      }));
      setJobTitleSelectList(selectList);
    }
  );

  const handleFormSubmit = form.onSubmit((formValues) => {
    setPrevFormValues(formValues);
    if (!MiscUtils.isEquals(formValues, prevFormValues)) {
      const requestBody: EmployeeRequest = {
        user: {
          username: formValues['user.username'],
          password: formValues['user.password'] || null,
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
          roles: [{ id: EmployeeConfigs.EMPLOYEE_ROLE_ID }],
        },
        officeId: Number(formValues.officeId),
        departmentId: Number(formValues.departmentId),
        jobTypeId: Number(formValues.jobTypeId),
        jobLevelId: Number(formValues.jobLevelId),
        jobTitleId: Number(formValues.jobTitleId),
      };
      updateApi.mutate(requestBody);
    }
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
      value: String(EmployeeConfigs.EMPLOYEE_ROLE_ID),
      label: 'Nhân viên',
    },
  ];

  return {
    employee,
    form,
    handleFormSubmit,
    userGenderSelectList,
    provinceSelectList,
    wardSelectList,
    userStatusSelectList,
    userRoleSelectList,
    officeSelectList,
    departmentSelectList,
    jobTypeSelectList,
    jobLevelSelectList,
    jobTitleSelectList,
  };
}

export default useEmployeeUpdateViewModel;
