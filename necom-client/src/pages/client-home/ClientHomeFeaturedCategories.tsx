import React from 'react';
import {
  Anchor,
  Box,
  Card,
  createStyles,
  Group,
  Image,
  SimpleGrid,
  Stack,
  Text,
  Title
} from '@mantine/core';
import { Link } from 'react-router-dom';

const featuredCategoriesData = [
  {
    name: 'Bàn ghế',
    slug: 'ban-ghe',
    image: '/images/categories/ban-ghe.jpg',
  },
  {
    name: 'Đèn trang trí',
    slug: 'den-chieu-sang',
    image: '/images/categories/den-trang-tri.jpg',
  },
  {
    name: 'Kệ tủ',
    slug: 'tu-ke',
    image: '/images/categories/ke-tu.jpg',
  },
  {
    name: 'Đồ bếp',
    slug: 'do-bep',
    image: '/images/categories/do-bep.jpg',
  },
  {
    name: 'Decor',
    slug: 'decor',
    image: '/images/categories/decor.jpg',
  },
  {
    name: 'Văn phòng tại nhà',
    slug: 'van-phong-tai-nha',
    image: '/images/categories/van-phong.jpg',
  },
];

const useStyles = createStyles((theme) => ({
  sectionTitle: {
    fontSize: 24,
    fontWeight: 700,
    color: '#111827',
    letterSpacing: '-0.01em',
  },
  viewAllLink: {
    color: '#059669',
    fontWeight: 600,
    fontSize: theme.fontSizes.sm,
    textDecoration: 'none',
    display: 'inline-flex',
    alignItems: 'center',
    gap: 4,
    transition: 'gap 0.2s ease',
    '&:hover': {
      textDecoration: 'none',
      color: '#047857',
      gap: 8,
    },
  },
  categoryCard: {
    backgroundColor: theme.colorScheme === 'dark' ? theme.colors.dark[6] : '#f3f6f3',
    borderRadius: theme.radius.lg,
    border: `1px solid ${theme.colorScheme === 'dark' ? theme.colors.dark[5] : 'rgba(0,0,0,0.03)'}`,
    padding: '16px 12px 14px',
    height: 175,
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    justifyContent: 'space-between',
    textDecoration: 'none',
    transition: 'all 0.25s ease',
    cursor: 'pointer',
    '&:hover': {
      transform: 'translateY(-4px)',
      boxShadow: '0 10px 24px rgba(0,0,0,0.06)',
      backgroundColor: theme.colorScheme === 'dark' ? theme.colors.dark[5] : '#edf3ed',
      borderColor: 'rgba(5, 150, 105, 0.25)',
    },
  },
  imageWrapper: {
    width: '100%',
    height: 110,
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    overflow: 'hidden',
  },
  categoryImage: {
    maxHeight: 105,
    maxWidth: '100%',
    objectFit: 'contain',
    borderRadius: theme.radius.md,
    transition: 'transform 0.3s ease',
    '&:hover': {
      transform: 'scale(1.05)',
    },
  },
  categoryName: {
    fontSize: 14,
    fontWeight: 600,
    color: theme.colorScheme === 'dark' ? theme.colors.dark[0] : '#1f2937',
    textAlign: 'center',
    lineHeight: 1.25,
    marginTop: 6,
  },
}));

function ClientHomeFeaturedCategories() {
  const { classes } = useStyles();

  return (
    <Stack spacing="md">
      <Group position="apart">
        <Title order={2} className={classes.sectionTitle}>
          Danh mục nổi bật
        </Title>
        <Anchor component={Link} to="/all-categories" className={classes.viewAllLink}>
          Xem tất cả →
        </Anchor>
      </Group>

      <SimpleGrid
        cols={6}
        spacing="md"
        breakpoints={[
          { maxWidth: 'lg', cols: 6, spacing: 'sm' },
          { maxWidth: 'md', cols: 3, spacing: 'sm' },
          { maxWidth: 'xs', cols: 2, spacing: 'xs' },
        ]}
      >
        {featuredCategoriesData.map((category) => (
          <Card
            key={category.slug}
            component={Link}
            to={`/category/${category.slug}`}
            className={classes.categoryCard}
          >
            <Box className={classes.imageWrapper}>
              <Image
                src={category.image}
                alt={category.name}
                className={classes.categoryImage}
                imageProps={{ loading: 'lazy' }}
              />
            </Box>
            <Text className={classes.categoryName}>
              {category.name}
            </Text>
          </Card>
        ))}
      </SimpleGrid>
    </Stack>
  );
}

export default ClientHomeFeaturedCategories;
