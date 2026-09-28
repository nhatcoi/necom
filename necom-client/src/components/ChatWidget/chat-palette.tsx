import React from 'react';
import { Box, MantineTheme, useMantineTheme } from '@mantine/core';
import { Armchair } from 'tabler-icons-react';

/**
 * Bảng màu chat theo nhận diện Japandi của Necom: xanh rừng (emerald đậm), nền cát/kem ấm, viền màu gỗ nhạt.
 */
export interface ChatPalette {
  forest: string;
  forestSoft: string;
  sand: string;
  sandStrong: string;
  sandBorder: string;
  cream: string;
  wood: string;
  text: string;
}

export function getChatPalette(theme: MantineTheme): ChatPalette {
  const dark = theme.colorScheme === 'dark';
  return {
    forest: dark ? theme.colors[theme.primaryColor][4] : '#0B4F3C',
    forestSoft: dark ? theme.fn.rgba(theme.colors[theme.primaryColor][8], 0.35) : '#E3EFE8',
    sand: dark ? theme.colors.dark[5] : '#F5F0E8',
    sandStrong: dark ? theme.colors.dark[4] : '#ECE3D5',
    sandBorder: dark ? theme.colors.dark[4] : '#E4D9C7',
    cream: dark ? theme.colors.dark[6] : '#FFFDF9',
    wood: dark ? '#C9A27A' : '#A67C52',
    text: dark ? theme.colors.dark[0] : '#2F2A24',
  };
}

export function useChatPalette(): ChatPalette {
  return getChatPalette(useMantineTheme());
}

/**
 * Avatar trợ lý Necom: ghế bành trên nền cát, thay cho icon AI chung chung.
 */
export function NecomAssistantAvatar({ size = 30, inverted = false }: { size?: number, inverted?: boolean }) {
  const palette = useChatPalette();
  return (
    <Box
      sx={{
        width: size,
        height: size,
        flexShrink: 0,
        borderRadius: '50%',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        backgroundColor: inverted ? palette.cream : palette.sandStrong,
        border: `1px solid ${inverted ? 'transparent' : palette.sandBorder}`,
        color: palette.forest,
      }}
    >
      <Armchair size={Math.round(size * 0.55)} strokeWidth={1.75}/>
    </Box>
  );
}
