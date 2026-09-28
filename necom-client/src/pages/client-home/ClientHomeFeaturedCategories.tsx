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
    image: 'https://images.unsplash.com/photo-1592078615290-033ee584e267?w=400&auto=format&fit=crop&q=80',
  },
  {
    name: 'Đèn trang trí',
    slug: 'den-chieu-sang',
    image: 'https://images.unsplash.com/photo-1513506003901-1e6a229e2d15?w=400&auto=format&fit=crop&q=80',
  },
  {
    name: 'Kệ tủ',
    slug: 'tu-ke',
    image: 'https://images.unsplash.com/photo-1594671581674-5c9b5d271312?w=400&auto=format&fit=crop&q=80',
  },
  {
    name: 'Đồ bếp',
    slug: 'do-bep',
    image: 'https://images.unsplash.com/photo-1584990347449-3997d81a9540?w=400&auto=format&fit=crop&q=80',
  },
  {
    name: 'Decor',
    slug: 'decor',
    image: 'https://images.unsplash.com/photo-1485955900006-10f4d324d411?w=400&auto=format&fit=crop&q=80',
  },
  {
    name: 'Văn phòng tại nhà',
    slug: 'van-phong-tai-nha',
    image: 'https://images.unsplash.com/photo-1518455027359-f3f8164ba6bd?w=400&auto=format&fit=crop&q=80',
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
    backgroundColor: theme.colorScheme === 'dark' ? theme.colors.dark[6] : '#f4f7f4',
    borderRadius: theme.radius.lg,
    border: `1px solid ${theme.colorScheme === 'dark' ? theme.colors.dark[5] : 'rgba(0,0,0,0.03)'}`,
    padding: '20px 14px 16px',
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    justifyContent: 'center',
    textDecoration: 'none',
    transition: 'all 0.25s ease',
    cursor: 'pointer',
    '&:hover': {
      transform: 'translateY(-4px)',
      boxShadow: '0 8px 24px rgba(0,0,0,0.06)',
      backgroundColor: theme.colorScheme === 'dark' ? theme.colors.dark[5] : '#edf4ed',
      borderColor: 'rgba(5, 150, 105, 0.2)',
    },
  },
  imageWrapper: {
    width: '100%',
    height: 110,
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: 10,
  },
  categoryImage: {
    maxHeight: 100,
    maxWidth: '85%',
    objectFit: 'contain',
    borderRadius: theme.radius.md,
    mixBlendMode: theme.colorScheme === 'dark' ? 'normal' : 'multiply',
  },
  categoryName: {
    fontSize: 14,
    fontWeight: 600,
    color: theme.colorScheme === 'dark' ? theme.colors.dark[0] : '#1f2937',
    textAlign: 'center',
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
