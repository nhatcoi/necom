import { useEffect, useRef } from 'react';
import { UseFormReturnType } from '@mantine/form/lib/use-form';

function useSelectAddress<T>(
  form: UseFormReturnType<T>,
  provinceIdKey: keyof T,
  secondKey: keyof T,
  thirdKey?: keyof T
) {
  // If thirdKey is provided: secondKey = districtIdKey, thirdKey = wardIdKey
  // If thirdKey is omitted: secondKey = wardIdKey (2-tier: province -> ward)
  const districtIdKey = thirdKey ? secondKey : undefined;
  const wardIdKey = thirdKey ? thirdKey : secondKey;

  const firstRender = useRef(true);

  useEffect(() => {
    if (!firstRender.current) {
      if (districtIdKey && form.values[districtIdKey] !== null) {
        form.setFieldValue(districtIdKey, null as T[keyof T]);
      }
      form.values[wardIdKey] !== null && form.setFieldValue(wardIdKey, null as T[keyof T]);
    }
  }, [form.values[provinceIdKey]]);

  useEffect(() => {
    if (!firstRender.current && districtIdKey) {
      form.values[wardIdKey] !== null && form.setFieldValue(wardIdKey, null as T[keyof T]);
    }
  }, [districtIdKey ? form.values[districtIdKey] : null]);

  useEffect(() => {
    firstRender.current = false;
  }, []);
}

export default useSelectAddress;
