import React from 'react';
import { Group, Pagination, Skeleton, Stack, Text, useMantineTheme } from '@mantine/core';
import NestProductCard from 'components/Nest/NestProductCard';
import ApplicationConstants from 'constants/ApplicationConstants';
import { useQuery } from 'react-query';
import FetchUtils, { ErrorMessage, ListResponse } from 'utils/FetchUtils';
import { ClientListedProductResponse } from 'types';
import ResourceURL from 'constants/ResourceURL';
import NotifyUtils from 'utils/NotifyUtils';
import { AlertTriangle, Marquee } from 'tabler-icons-react';
import useClientCategoryStore from 'stores/use-client-category-store';

interface ClientCategoryProductsProps {
  categorySlug: string;
}

function ClientCategoryProducts({ categorySlug }: ClientCategoryProductsProps) {
  const theme = useMantineTheme();

  const { activePage, updateActivePage } = useClientCategoryStore();

  const {
    productResponses,
    isLoadingProductResponses,
    isErrorProductResponses,
  } = useGetAllCategoryProductsApi(categorySlug);
  const products = productResponses as ListResponse<ClientListedProductResponse>;

  if (isLoadingProductResponses) {
    return (
      <Stack>
        {Array(5).fill(0).map((_, index) => (
          <Skeleton key={index} height={50} radius="md"/>
        ))}
      </Stack>
    );
  }

  if (isErrorProductResponses) {
    return (
      <Stack my={theme.spacing.xl} sx={{ alignItems: 'center', color: theme.colors.pink[6] }}>
        <AlertTriangle size={125} strokeWidth={1}/>
        <Text size="xl" weight={500}>Đã có lỗi xảy ra</Text>
      </Stack>
    );
  }

  if (products.totalElements === 0) {
    return (
      <Stack my={theme.spacing.xl} sx={{ alignItems: 'center', color: theme.colors.gray[6] }}>
        <Marquee size={125} strokeWidth={1}/>
        <Text size="xl" weight={500}>Không có sản phẩm</Text>
      </Stack>
    );
  }

  return (
    <>
      <div className="nest-product-grid nest-product-grid--listing">
        {products.content.map(product => <NestProductCard key={product.productId} product={product}/>)}
      </div>

      <Group position="apart" mt={theme.spacing.lg}>
        <Pagination
          page={activePage}
          total={products.totalPages}
          onChange={(page: number) => (page !== activePage) && updateActivePage(page)}
        />
        <Text>
          <Text component="span" weight={500}>Trang {activePage}</Text>
          <span> / {products.totalPages}</span>
        </Text>
      </Group>
    </>
  );
}

function useGetAllCategoryProductsApi(categorySlug: string) {
  const {
    totalProducts,
    activePage,
    activeSort,
    activeSearch,
    activeSaleable,
    activeBrandFilter,
    activePriceFilter,
    updateTotalProducts,
  } = useClientCategoryStore();

  const requestParams = {
    page: activePage,
    size: ApplicationConstants.DEFAULT_CLIENT_CATEGORY_PAGE_SIZE,
    filter: [`category.slug==${categorySlug}`, activeBrandFilter, activePriceFilter].filter(Boolean).join(';'),
    sort: activeSort,
    search: activeSearch,
    newable: true,
    saleable: activeSaleable,
  };

  const {
    data: productResponses,
    isLoading: isLoadingProductResponses,
    isError: isErrorProductResponses,
  } = useQuery<ListResponse<ClientListedProductResponse>, ErrorMessage>(
    ['client-api', 'products', 'getAllProducts', requestParams],
    () => FetchUtils.get(ResourceURL.CLIENT_PRODUCT, requestParams),
    {
      onSuccess: (productResponses) =>
        (totalProducts !== productResponses.totalElements) && updateTotalProducts(productResponses.totalElements),
      onError: () => NotifyUtils.simpleFailed('Lấy dữ liệu không thành công'),
      refetchOnWindowFocus: false,
      keepPreviousData: true,
    }
  );

  return { productResponses, isLoadingProductResponses, isErrorProductResponses };
}

export default ClientCategoryProducts;
