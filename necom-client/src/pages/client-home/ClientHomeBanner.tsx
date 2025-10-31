import React from 'react';
import { Box, Button, createStyles, Grid, Group, Stack, Text, Title, useMantineTheme } from '@mantine/core';
import { ClientCarousel } from 'components';

// Local SVG icon assets for hero badges/categories
import cameraIcon from 'assets/icons/camera.svg';
import mobileIcon from 'assets/icons/mobile.svg';
import devicesIcon from 'assets/icons/devices.svg';
import houseIcon from 'assets/icons/house.svg';

const useStyles = createStyles((theme) => ({
  rightBanner: {
    flexWrap: 'unset',
    backgroundColor: theme.colorScheme === 'dark' ? theme.colors.dark[4] : theme.colors.gray[1],
    borderRadius: theme.radius.md,
  },
  leftHero: {
    position: 'relative',
    height: '100%',
    minHeight: 315,
    borderRadius: theme.radius.md,
    overflow: 'hidden',
    display: 'flex',
    alignItems: 'center',
    padding: theme.spacing.xl,
  },
  pill: {
    display: 'inline-flex',
    alignItems: 'center',
    gap: 8,
    padding: '8px 12px',
    borderRadius: 999,
    backgroundColor: theme.white,
    boxShadow: theme.shadows.sm,
  },
  pillImg: {
    width: 18,
    height: 18,
  },
  featureImg: {
    width: 56,
    height: 56,
  },
}));

function ClientHomeBanner() {
  const theme = useMantineTheme();
  const { classes } = useStyles();

  return (
    <Grid>
      <Grid.Col md={7} lg={8}>
        <ClientCarousel>
          <Box className={classes.leftHero}
               sx={{ backgroundImage: theme.fn.linearGradient(110, theme.colors.indigo[6], theme.colors.cyan[5]) }}>
            <Stack spacing={12} sx={{ maxWidth: 520 }}>
              <Title order={2} color={theme.white} sx={{ lineHeight: 1.15 }}>
                Khám phá thiết bị điện tử mới nhất
              </Title>
              <Text color={theme.white} size="sm" sx={{ opacity: 0.9 }}>
                Săn deal cực sốc mỗi ngày - giao nhanh, bảo hành chính hãng.
              </Text>
              <Group spacing="sm" mt={6}>
                <Button radius="md" size="md" color="yellow" variant="filled">
                  Mua ngay
                </Button>
                <Button radius="md" size="md" variant="white" color="dark">
                  Xem khuyến mãi
                </Button>
              </Group>
              <Group spacing={10} mt={12}>
                <Box className={classes.pill}>
                  <img className={classes.pillImg} src={mobileIcon} alt="Điện thoại"/>
                  <Text size="sm" weight={500}>Điện thoại</Text>
                </Box>
                <Box className={classes.pill}>
                  <img className={classes.pillImg} src={devicesIcon} alt="Laptop"/>
                  <Text size="sm" weight={500}>Laptop</Text>
                </Box>
                <Box className={classes.pill}>
                  <img className={classes.pillImg} src={cameraIcon} alt="Camera"/>
                  <Text size="sm" weight={500}>Camera</Text>
                </Box>
                <Box className={classes.pill}>
                  <img className={classes.pillImg} src={houseIcon} alt="Smart Home"/>
                  <Text size="sm" weight={500}>Smart Home</Text>
                </Box>
              </Group>
            </Stack>
          </Box>

          <Box className={classes.leftHero}
               sx={{ backgroundImage: theme.fn.linearGradient(110, theme.colors.violet[6], theme.colors.pink[5]) }}>
            <Stack spacing={12} sx={{ maxWidth: 520 }}>
              <Title order={2} color={theme.white} sx={{ lineHeight: 1.15 }}>
                Siêu ưu đãi phụ kiện công nghệ
              </Title>
              <Text color={theme.white} size="sm" sx={{ opacity: 0.9 }}>
                Tai nghe, bàn phím, chuột gaming giảm đến 50%.
              </Text>
              <Group spacing="sm" mt={6}>
                <Button radius="md" size="md" color="yellow" variant="filled">Khám phá</Button>
                <Button radius="md" size="md" variant="white" color="dark">Xem thêm</Button>
              </Group>
            </Stack>
          </Box>

          <Box className={classes.leftHero}
               sx={{ backgroundImage: theme.fn.linearGradient(110, theme.colors.teal[6], theme.colors.lime[5]) }}>
            <Stack spacing={12} sx={{ maxWidth: 520 }}>
              <Title order={2} color={theme.white} sx={{ lineHeight: 1.15 }}>
                Nâng cấp không gian làm việc
              </Title>
              <Text color={theme.white} size="sm" sx={{ opacity: 0.9 }}>
                Màn hình, ghế công thái học, hub kết nối… giá tốt.
              </Text>
              <Group spacing="sm" mt={6}>
                <Button radius="md" size="md" color="yellow" variant="filled">Mua ngay</Button>
                <Button radius="md" size="md" variant="white" color="dark">Tham khảo</Button>
              </Group>
            </Stack>
          </Box>
        </ClientCarousel>
      </Grid.Col>
      <Grid.Col md={5} lg={4}>
        <Stack>
          <Group py="sm" px="md" className={classes.rightBanner}>
            <img className={classes.featureImg} src={mobileIcon} alt="Miễn phí vận chuyển"/>
            <Stack spacing={theme.spacing.xs / 4}>
              <Text size="md" weight={500}>Miễn phí vận chuyển</Text>
              <Text size="sm">100% đơn hàng đều được miễn phí vận chuyển khi thanh toán trước.</Text>
            </Stack>
          </Group>
          <Group py="sm" px="md" className={classes.rightBanner}>
            <img className={classes.featureImg} src={devicesIcon} alt="Bảo hành tận tâm"/>
            <Stack spacing={theme.spacing.xs / 4}>
              <Text size="md" weight={500}>Bảo hành tận tâm</Text>
              <Text size="sm">Cam kết hỗ trợ khách hàng tận tình trong suốt thời gian bảo hành.</Text>
            </Stack>
          </Group>
          <Group py="sm" px="md" className={classes.rightBanner}>
            <img className={classes.featureImg} src={houseIcon} alt="Đổi trả 1-1"/>
            <Stack spacing={theme.spacing.xs / 4}>
              <Text size="md" weight={500}>Đổi trả 1-1 hoặc hoàn tiền</Text>
              <Text size="sm">Nếu phát sinh lỗi hoặc sản phẩm chưa đáp ứng được nhu cầu.</Text>
            </Stack>
          </Group>
        </Stack>
      </Grid.Col>
    </Grid>
  );
}

export default ClientHomeBanner;
