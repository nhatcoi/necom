import { z } from 'zod';
import { Configs, EntityPropertySchema, EntityPropertyType, TitleLink } from 'types';
import ResourceURL from 'constants/ResourceURL';
import MessageUtils from 'utils/MessageUtils';
import PageConfigs from 'pages/PageConfigs';
import ManagerPath from 'constants/ManagerPath';
import AddressConfigs from 'pages/address/AddressConfigs';

class WardConfigs extends Configs {
  static managerPath = ManagerPath.WARD;
  static resourceUrl = ResourceURL.WARD;
  static resourceKey = 'wards';
  static createTitle = 'Thêm phường xã';
  static updateTitle = 'Cập nhật phường xã';
  static manageTitle = 'Quản lý phường xã';

  static manageTitleLinks: TitleLink[] = AddressConfigs.manageTitleLinks;

  protected static _rawProperties = {
    ...PageConfigs.getProperties(true, true, true),
    name: {
      label: 'Tên phường xã',
      type: EntityPropertyType.STRING,
      isShowInTable: true,
    },
    code: {
      label: 'Mã phường xã',
      type: EntityPropertyType.STRING,
      isShowInTable: true,
    },
    'province.name': {
      label: 'Tên tỉnh thành',
      type: EntityPropertyType.STRING,
      isShowInTable: true,
    },
    'province.code': {
      label: 'Mã tỉnh thành',
      type: EntityPropertyType.STRING,
      isShowInTable: true,
    },
    provinceId: {
      label: 'Tỉnh thành',
      type: EntityPropertyType.NUMBER,
      isNotAddToSortCriteria: true,
      isNotAddToFilterCriteria: true,
    },
  };

  static properties = WardConfigs._rawProperties as
    EntityPropertySchema<typeof WardConfigs._rawProperties & typeof PageConfigs.properties>;

  static initialCreateUpdateFormValues = {
    name: '',
    code: '',
    provinceId: null as string | null,
  };

  static createUpdateFormSchema = z.object({
    name: z.string().min(2, MessageUtils.min(WardConfigs.properties.name.label, 2)),
    code: z.string().max(35, MessageUtils.max(WardConfigs.properties.code.label, 35)),
    provinceId: z.string(),
  });
}

export default WardConfigs;
