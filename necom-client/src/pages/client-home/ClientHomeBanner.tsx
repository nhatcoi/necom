import React, { useState, useEffect } from 'react';
import {
  ActionIcon,
  Badge,
  Box,
  Button,
  Card,
  createStyles,
  Grid,
  Group,
  SimpleGrid,
  Stack,
  Text,
  Title,
  UnstyledButton,
  useMantineTheme
} from '@mantine/core';
import { Link } from 'react-router-dom';
import {
  Archive,
  Armchair,
  Box as BoxIcon,
  ChevronLeft,
  ChevronRight,
  DeviceDesktop,
  GridDots,
  Headset,
  Lamp,
  Plant,
  ShieldCheck,
  Sofa,
  ToolsKitchen2,
  TruckDelivery
} from 'tabler-icons-react';

const categoryMenuItems = [
  { name: 'Bàn ghế', slug: 'ban-ghe', icon: Armchair },
  { name: 'Sofa', slug: 'sofa', icon: Sofa },
  { name: 'Tủ kệ', slug: 'tu-ke', icon: Archive },
  { name: 'Đèn & chiếu sáng', slug: 'den-chieu-sang', icon: Lamp },
  { name: 'Đồ decor', slug: 'decor', icon: Plant },
  { name: 'Đồ bếp & ăn uống', slug: 'do-bep', icon: ToolsKitchen2 },
  { name: 'Đồ lưu trữ', slug: 'do-luu-tru', icon: BoxIcon },
  { name: 'Văn phòng tại nhà', slug: 'van-phong-tai-nha', icon: DeviceDesktop },
  { name: 'Xem tất cả', slug: 'all-categories', icon: GridDots, isAll: true },
];

const bannerSlides = [
  {
    tag: 'Không gian sống tốt hơn',
    title: 'Nâng cấp\nngôi nhà của bạn',
    subtitle: 'Nội thất hiện đại • Đồ gia dụng tiện ích\nDecor đẹp • Giá tốt',
    ctaText: 'Mua ngay →',
    ctaLink: '/category/ban-ghe',
    bgImage: 'https://images.unsplash.com/photo-1618221195710-dd6b41faaea6?w=1400&auto=format&fit=crop&q=80',
  },
  {
    tag: 'Tối giản & Tinh tế',
    title: 'Phong cách Japandi\ncho tổ ấm',
    subtitle: 'Sự giao thoa hoàn mỹ giữa nét mộc mạc Nhật Bản\nvà phong cách Bắc Âu thanh lịch',
    ctaText: 'Khám phá ngay →',
    ctaLink: '/category/sofa',
    bgImage: 'https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=1400&auto=format&fit=crop&q=80',
  },
  {
    tag: 'Home Office chuẩn gu',
    title: 'Góc làm việc\ntràn đầy cảm hứng',
    subtitle: 'Bàn ghế gỗ sồi, kệ nâng màn hình\nvà phụ kiện bàn làm việc tinh gọn',
    ctaText: 'Xem bộ sưu tập →',
    ctaLink: '/category/van-phong-tai-nha',
    bgImage: 'https://images.unsplash.com/photo-1518455027359-f3f8164ba6bd?w=1400&auto=format&fit=crop&q=80',
  },
];

const useStyles = createStyles((theme) => ({
  categorySidebar: {
    backgroundColor: theme.colorScheme === 'dark' ? theme.colors.dark[7] : theme.white,
    borderRadius: theme.radius.lg,
    border: `1px solid ${theme.colorScheme === 'dark' ? theme.colors.dark[5] : theme.colors.gray[2]}`,
    padding: `${theme.spacing.sm}px 0`,
    height: '100%',
    display: 'flex',
    flexDirection: 'column',
    justifyContent: 'space-between',
    boxShadow: '0 1px 3px rgba(0,0,0,0.04)',
  },
  categoryItem: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'space-between',
    padding: '9px 18px',
    color: theme.colorScheme === 'dark' ? theme.colors.dark[0] : theme.colors.gray[8],
    textDecoration: 'none',
    transition: 'all 0.15s ease',
    fontSize: theme.fontSizes.sm,
    fontWeight: 500,
    '&:hover': {
      backgroundColor: theme.colorScheme === 'dark' ? theme.colors.dark[6] : '#f0fdf4',
      color: theme.colors.emerald ? theme.colors.emerald[6] : '#059669',
      paddingLeft: 22,
    },
  },
  heroBanner: {
    position: 'relative',
    height: '100%',
    minHeight: 380,
    borderRadius: theme.radius.lg,
    overflow: 'hidden',
    display: 'flex',
    alignItems: 'center',
    backgroundSize: 'cover',
    backgroundPosition: 'center',
    padding: theme.spacing.xl * 1.5,
    transition: 'background-image 0.5s ease-in-out',
    boxShadow: '0 4px 20px rgba(0,0,0,0.06)',
  },
  heroOverlay: {
    position: 'absolute',
    inset: 0,
    background: 'linear-gradient(90deg, rgba(255,255,255,0.92) 0%, rgba(255,255,255,0.75) 45%, rgba(255,255,255,0.1) 100%)',
    zIndex: 1,
  },
  heroContent: {
    position: 'relative',
    zIndex: 2,
    maxWidth: 480,
  },
  navArrow: {
    position: 'absolute',
    top: '50%',
    transform: 'translateY(-50%)',
    zIndex: 3,
    backgroundColor: 'rgba(255, 255, 255, 0.9)',
    backdropFilter: 'blur(4px)',
    border: '1px solid rgba(0,0,0,0.06)',
    boxShadow: '0 2px 8px rgba(0,0,0,0.12)',
    color: '#374151',
    '&:hover': {
      backgroundColor: '#ffffff',
      color: '#059669',
    },
  },
  dotsContainer: {
    position: 'absolute',
    bottom: 16,
    left: '50%',
    transform: 'translateX(-50%)',
    zIndex: 3,
    display: 'flex',
    gap: 6,
  },
  dot: {
    height: 8,
    borderRadius: 4,
    transition: 'all 0.3s ease',
    cursor: 'pointer',
  },
  trustCard: {
    backgroundColor: theme.colorScheme === 'dark' ? theme.colors.dark[7] : theme.white,
    borderRadius: theme.radius.md,
    border: `1px solid ${theme.colorScheme === 'dark' ? theme.colors.dark[5] : theme.colors.gray[2]}`,
    padding: '16px 20px',
    boxShadow: '0 1px 2px rgba(0,0,0,0.03)',
  },
  trustIcon: {
    color: '#059669',
  },
}));

function ClientHomeBanner() {
  const theme = useMantineTheme();
  const { classes } = useStyles();
  const [currentSlide, setCurrentSlide] = useState(0);

  useEffect(() => {
    const timer = setInterval(() => {
      setCurrentSlide((prev) => (prev + 1) % bannerSlides.length);
    }, 6000);
    return () => clearInterval(timer);
  }, []);

  const slide = bannerSlides[currentSlide];

  const handlePrev = () => {
    setCurrentSlide((prev) => (prev - 1 + bannerSlides.length) % bannerSlides.length);
  };

  const handleNext = () => {
    setCurrentSlide((prev) => (prev + 1) % bannerSlides.length);
  };

  return (
    <Stack spacing="lg">
      <Grid gutter="md">
        {/* Left: Category Sidebar */}
        <Grid.Col md={3} lg={3} sx={{ display: 'none', [theme.fn.largerThan('sm')]: { display: 'block' } }}>
          <Box className={classes.categorySidebar}>
            {categoryMenuItems.map((item) => {
              const IconComponent = item.icon;
              const linkTo = item.isAll ? '/all-categories' : `/category/${item.slug}`;
              return (
                <UnstyledButton
                  key={item.slug}
                  component={Link}
                  to={linkTo}
                  className={classes.categoryItem}
                >
                  <Group spacing={10}>
                    <IconComponent size={18} strokeWidth={1.5} color="#4b5563" />
                    <Text size="sm">{item.name}</Text>
                  </Group>
                  <ChevronRight size={14} color="#9ca3af" strokeWidth={1.5} />
                </UnstyledButton>
              );
            })}
          </Box>
        </Grid.Col>

        {/* Right: Hero Banner */}
        <Grid.Col sm={12} md={9} lg={9}>
          <Box
            className={classes.heroBanner}
            sx={{ backgroundImage: `url(${slide.bgImage})` }}
          >
            <Box className={classes.heroOverlay} />

            {/* Left Nav Arrow */}
            <ActionIcon
              className={classes.navArrow}
              sx={{ left: 14 }}
              radius="xl"
              size="lg"
              onClick={handlePrev}
            >
              <ChevronLeft size={20} />
            </ActionIcon>

            {/* Right Nav Arrow */}
            <ActionIcon
              className={classes.navArrow}
              sx={{ right: 14 }}
              radius="xl"
              size="lg"
              onClick={handleNext}
            >
              <ChevronRight size={20} />
            </ActionIcon>

            {/* Slide Content */}
            <Box className={classes.heroContent}>
              <Stack spacing={12}>
                <Badge
                  variant="filled"
                  radius="xl"
                  size="md"
                  sx={{
                    backgroundColor: '#ecfdf5',
                    color: '#065f46',
                    alignSelf: 'flex-start',
                    textTransform: 'none',
                    fontWeight: 600,
                    letterSpacing: '0.02em',
                    padding: '8px 14px',
                  }}
                >
                  {slide.tag}
                </Badge>

                <Title
                  order={1}
                  sx={{
                    fontSize: 34,
                    lineHeight: 1.15,
                    color: '#111827',
                    fontWeight: 700,
                    whiteSpace: 'pre-line',
                    letterSpacing: '-0.02em',
                    [theme.fn.largerThan('md')]: {
                      fontSize: 40,
                    },
                  }}
                >
                  {slide.title}
                </Title>

                <Text
                  size="md"
                  sx={{
                    color: '#4b5563',
                    lineHeight: 1.5,
                    whiteSpace: 'pre-line',
                    fontWeight: 400,
                  }}
                >
                  {slide.subtitle}
                </Text>

                <Group mt={8}>
                  <Button
                    component={Link}
                    to={slide.ctaLink}
                    radius="md"
                    size="md"
                    sx={{
                      backgroundColor: '#059669',
                      padding: '10px 24px',
                      fontSize: 15,
                      fontWeight: 600,
                      '&:hover': {
                        backgroundColor: '#047857',
                      },
                    }}
                  >
                    {slide.ctaText}
                  </Button>
                </Group>
              </Stack>
            </Box>

            {/* Pagination Dots */}
            <Box className={classes.dotsContainer}>
              {bannerSlides.map((_, index) => (
                <Box
                  key={index}
                  className={classes.dot}
                  sx={{
                    width: currentSlide === index ? 24 : 8,
                    backgroundColor: currentSlide === index ? '#059669' : 'rgba(156, 163, 175, 0.6)',
                  }}
                  onClick={() => setCurrentSlide(index)}
                />
              ))}
            </Box>
          </Box>
        </Grid.Col>
      </Grid>

      {/* Trust Badges: 4 Value Propositions */}
      <SimpleGrid
        cols={4}
        breakpoints={[
          { maxWidth: 'md', cols: 2, spacing: 'sm' },
          { maxWidth: 'xs', cols: 1, spacing: 'xs' },
        ]}
      >
        <Card className={classes.trustCard}>
          <Group spacing={14} noWrap>
            <TruckDelivery size={36} className={classes.trustIcon} strokeWidth={1.5} />
            <Stack spacing={2}>
              <Text size="sm" weight={600} color="#111827">Miễn phí vận chuyển</Text>
              <Text size="xs" color="dimmed">Đơn từ 1 triệu đồng</Text>
            </Stack>
          </Group>
        </Card>

        <Card className={classes.trustCard}>
          <Group spacing={14} noWrap>
            <ShieldCheck size={36} className={classes.trustIcon} strokeWidth={1.5} />
            <Stack spacing={2}>
              <Text size="sm" weight={600} color="#111827">Bảo hành chính hãng</Text>
              <Text size="xs" color="dimmed">Cam kết chất lượng</Text>
            </Stack>
          </Group>
        </Card>

        <Card className={classes.trustCard}>
          <Group spacing={14} noWrap>
            <BoxIcon size={36} className={classes.trustIcon} strokeWidth={1.5} />
            <Stack spacing={2}>
              <Text size="sm" weight={600} color="#111827">Đổi trả 1-1</Text>
              <Text size="xs" color="dimmed">Trong 7 ngày</Text>
            </Stack>
          </Group>
        </Card>

        <Card className={classes.trustCard}>
          <Group spacing={14} noWrap>
            <Headset size={36} className={classes.trustIcon} strokeWidth={1.5} />
            <Stack spacing={2}>
              <Text size="sm" weight={600} color="#111827">Hỗ trợ 24/7</Text>
              <Text size="xs" color="dimmed">Tư vấn tận tâm</Text>
            </Stack>
          </Group>
        </Card>
      </SimpleGrid>
    </Stack>
  );
}

export default ClientHomeBanner;
