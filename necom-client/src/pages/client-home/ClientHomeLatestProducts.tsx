import React from 'react';
import { Anchor, Group, SimpleGrid, Skeleton, Stack, Text, Title, useMantineTheme } from '@mantine/core';
import { AlertTriangle, Marquee } from 'tabler-icons-react';
import { ClientProductCard } from 'components';
import { useQuery } from 'react-query';
import FetchUtils, { ErrorMessage, ListResponse } from 'utils/FetchUtils';
import { ClientListedProductResponse } from 'types';
import ResourceURL from 'constants/ResourceURL';
import NotifyUtils from 'utils/NotifyUtils';
import { Link } from 'react-router-dom';

function ClientHomeLatestProducts() {
  const theme = useMantineTheme();

  const requestParams = { size: 10, newable: true, saleable: true };

  const {
    data: productResponses,
    isLoading: isLoadingProductResponses,
    isError: isErrorProductResponses,
  } = useQuery<ListResponse<ClientListedProductResponse>, ErrorMessage>(
    ['client-api', 'products', 'getAllProducts', requestParams],
    () => FetchUtils.get(ResourceURL.CLIENT_PRODUCT, requestParams),
    {
      onError: () => NotifyUtils.simpleFailed('Lấy dữ liệu không thành công'),
      refetchOnWindowFocus: false,
      keepPreviousData: true,
    }
  );
  const products = productResponses as ListResponse<ClientListedProductResponse>;

  let resultFragment;

  if (isLoadingProductResponses) {
    resultFragment = (
      <SimpleGrid
        cols={5}
        spacing="md"
        breakpoints={[
          { maxWidth: 'lg', cols: 5, spacing: 'sm' },
          { maxWidth: 'md', cols: 3, spacing: 'sm' },
          { maxWidth: 'xs', cols: 2, spacing: 'xs' },
        ]}
      >
        {Array(5).fill(0).map((_, index) => (
          <Skeleton key={index} height={320} radius="lg" />
        ))}
      </SimpleGrid>
    );
  }

  if (isErrorProductResponses) {
    resultFragment = (
      <Stack my={theme.spacing.xl} sx={{ alignItems: 'center', color: theme.colors.pink[6] }}>
        <AlertTriangle size={80} strokeWidth={1.5} />
        <Text size="lg" weight={500}>Đã có lỗi xảy ra khi tải sản phẩm</Text>
      </Stack>
    );
  }

  if (products && products.totalElements === 0) {
    resultFragment = (
      <Stack my={theme.spacing.xl} sx={{ alignItems: 'center', color: theme.colors.gray[6] }}>
        <Marquee size={80} strokeWidth={1.5} />
        <Text size="lg" weight={500}>Chưa có sản phẩm nào</Text>
      </Stack>
    );
  }

  if (products && products.totalElements > 0) {
    resultFragment = (
      <SimpleGrid
        cols={5}
        spacing="md"
        breakpoints={[
          { maxWidth: 'lg', cols: 5, spacing: 'md' },
          { maxWidth: 'md', cols: 3, spacing: 'sm' },
          { maxWidth: 'xs', cols: 2, spacing: 'xs' },
        ]}
      >
        {products.content.map((product) => (
          <ClientProductCard key={product.productId} product={product} />
        ))}
      </SimpleGrid>
    );
  }

  return (
    <Stack spacing="md">
      <Group position="apart">
        <Title
          order={2}
          sx={{
            fontSize: 24,
            fontWeight: 700,
            color: '#111827',
            letterSpacing: '-0.01em',
          }}
        >
          Sản phẩm nổi bật
        </Title>
        <Anchor
          component={Link}
          to="/search"
          sx={{
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
          }}
        >
          Xem tất cả →
        </Anchor>
      </Group>

      {resultFragment}
    </Stack>
  );
}

export default ClientHomeLatestProducts;
