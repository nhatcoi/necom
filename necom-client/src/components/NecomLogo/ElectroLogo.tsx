import React from 'react';
import { useMantineTheme } from '@mantine/core';

interface NecomLogoProps {
  width?: number;
}

function NecomLogo({ width = 120 }: NecomLogoProps) {
  useMantineTheme();

  return (
    <img src="/necom.svg" width={width} alt="Necom" />
  );
}

export default NecomLogo;
