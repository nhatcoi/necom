import {
  Anchor,
  Breadcrumbs,
  Button,
  Checkbox,
  Chip,
  Chips,
  Container,
  Grid,
  Group,
  Radio,
  RadioGroup,
  Stack,
  Text,
  TextInput,
  useMantineTheme
} from '@mantine/core';
import React, { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import MiscUtils from 'utils/MiscUtils';
import { ClientError } from 'components';
import { Adjustments, ArrowsDownUp, Search, X } from 'tabler-icons-react';
import useTitle from 'hooks/use-title';
import { useQuery } from 'react-query';
import FetchUtils, { ErrorMessage } from 'utils/FetchUtils';
import ResourceURL from 'constants/ResourceURL';
import NotifyUtils from 'utils/NotifyUtils';
import { ClientCategoryResponse, ClientFilterResponse } from 'types';
import ClientCategorySkeleton from 'pages/client-category/ClientCategorySkeleton';
import ClientCategoryProducts from 'pages/client-category/ClientCategoryProducts';
import useClientCategoryStore from 'stores/use-client-category-store';
import { useDebouncedValue } from '@mantine/hooks';

function ClientCategory() {
  const theme = useMantineTheme();

  const { slug } = useParams();

  const {
    totalProducts,
    activePage,
    activePriceFilter,
    activeBrandFilter,
    activeSort,
    activeSearch,
    activeSaleable,
    updateActivePage,
    updateActiveSort,
    updateActiveSearch,
    updateActiveSaleable,
    updateActiveBrandFilter,
    updateActivePriceFilter,
    resetClientCategoryState,
  } = useClientCategoryStore();

  // Search state
  const [searchQuery, setSearchQuery] = useState<string | null>(null);
  const [debouncedSearchQuery] = useDebouncedValue(searchQuery, 400);
  useEffect(() => {
    if (debouncedSearchQuery !== activeSearch) {
      updateActivePage(1);
      updateActiveSearch(debouncedSearchQuery);
    }
  }, [activeSearch, debouncedSearchQuery, updateActivePage, updateActiveSearch]);

  // Filter state
  const [priceOptions, setPriceOptions] = useState<string[]>([]);
  const [brandOptions, setBrandOptions] = useState<string[]>([]);

  // Reset all state
  useEffect(() => {
    setSearchQuery(null);
    setPriceOptions([]);
    setBrandOptions([]);
    resetClientCategoryState();
  }, [resetClientCategoryState, slug]);

  // Fetch category
  const { categoryResponse, isLoadingCategoryResponse, isErrorCategoryResponse } = useGetCategoryApi(slug as string);
  const category = categoryResponse as ClientCategoryResponse;
  useTitle(category?.categoryName);

  // Fetch filter
  const { filterResponse, isLoadingFilterResponse, isErrorFilterResponse } = useGetFilterApi(slug as string);
  const filter = filterResponse as ClientFilterResponse;

  // Composition
  const isLoading = isLoadingCategoryResponse || isLoadingFilterResponse;
  const isError = isErrorCategoryResponse || isErrorFilterResponse;

  if (isLoading) {
    return <ClientCategorySkeleton/>;
  }

  if (isError) {
    return <ClientError/>;
  }

  const handlePriceOptionChips = (priceOptions: string[]) => {
    const expressions = [];

    for (const priceOption of priceOptions) {
      const priceOptionArray = priceOption.split('-');
      if (priceOptionArray[1] === 'max') {
        expressions.push(`variants.price=bt=(${priceOptionArray[0]},1000000000)`);
      } else {
        expressions.push(`variants.price=bt=(${priceOptionArray[0]},${priceOptionArray[1]})`);
      }
    }

    setPriceOptions(priceOptions);
    updateActivePriceFilter(expressions.length > 0 ? `(${expressions.join(',')})` : null);
  };

  const handleBrandChips = (brandIds: string[]) => {
    setBrandOptions(brandIds);
    updateActiveBrandFilter(brandIds.length > 0 ? `brand.id=in=(${brandIds.join(',')})` : null);
  };

  const disabledResetButton = activePage === 1
    && activeBrandFilter === null
    && activePriceFilter === null
    && activeSort === null
    && searchQuery === null
    && !activeSaleable;

  const handleResetButton = () => {
    resetClientCategoryState();
    setSearchQuery(null);
    setPriceOptions([]);
    setBrandOptions([]);
  };

  return (
    <main>
      <Container size="xl">
        <Stack spacing={theme.spacing.xl * 2}>

          <header className="nest-page-head">
            <Breadcrumbs>
              <Anchor component={Link} to="/">
                Trang chủ
              </Anchor>
              {MiscUtils.makeCategoryBreadcrumbs(category).slice(0, -1).map(c => (
                <Anchor key={c.categorySlug} component={Link} to={'/category/' + c.categorySlug}>
                  {c.categoryName}
                </Anchor>
              ))}
              <Text color="dimmed">
                {category.categoryName}
              </Text>
            </Breadcrumbs>

            <h1>{category.categoryName}</h1>
            {totalProducts > 0 && <p className="nest-muted">{totalProducts} sản phẩm</p>}

            {category.categoryChildren.length > 0 && (
              <nav className="nest-subcategories" aria-label="Danh mục con">
                {category.categoryChildren.map(c => (
                  <Link key={c.categorySlug} to={'/category/' + c.categorySlug}>{c.categoryName}</Link>
                ))}
              </nav>
            )}
          </header>

          <Grid gutter="xl">
            <Grid.Col md={3} mb={theme.spacing.xl}>
              <Stack spacing="lg">
                <Group position="apart">
                  <Group spacing="xs">
                    <Adjustments size={18} strokeWidth={1.5}/>
                    <Text weight={500}>Bộ lọc</Text>
                  </Group>
                  <Button
                    variant="subtle"
                    color="gray"
                    size="xs"
                    compact
                    leftIcon={<X size={12}/>}
                    styles={{ leftIcon: { marginRight: 4 } }}
                    onClick={handleResetButton}
                    disabled={disabledResetButton}
                  >
                    Xóa bộ lọc
                  </Button>
                </Group>

                <Stack>
                  <Text weight={500}>Tìm kiếm</Text>
                  <TextInput
                    placeholder={'Tìm kiếm trong ' + category.categoryName}
                    icon={<Search size={16}/>}
                    value={searchQuery || ''}
                    onChange={(event) => setSearchQuery(event.currentTarget.value || null)}
                  />
                </Stack>

                <Stack>
                  <Text weight={500}>Khoảng giá</Text>
                  <Chips variant="filled" multiple value={priceOptions} onChange={handlePriceOptionChips}>
                    {MiscUtils.generatePriceOptions(filter.filterPriceQuartiles).map((priceOption, index) => (
                      <Chip key={index} value={priceOption.join('-')}>
                        {MiscUtils.readablePriceOption(priceOption)}
                      </Chip>
                    ))}
                  </Chips>
                </Stack>

                <Stack>
                  <Text weight={500}>Thương hiệu</Text>
                  {filter.filterBrands.length > 0
                    ? (
                      <Chips variant="filled" multiple value={brandOptions} onChange={handleBrandChips}>
                        {filter.filterBrands.map(brand => (
                          <Chip key={brand.brandId} value={brand.brandId}>{brand.brandName}</Chip>
                        ))}
                      </Chips>
                    ) : <Text sx={{ fontStyle: 'italic' }} color="dimmed">Không có tùy chọn</Text>}
                </Stack>

                <Stack>
                  <Text weight={500}>Khác</Text>
                  <Checkbox
                    label="Chỉ tính còn hàng"
                    checked={activeSaleable}
                    onChange={(event) => updateActiveSaleable(event.currentTarget.checked)}
                  />
                </Stack>
              </Stack>
            </Grid.Col>

            <Grid.Col md={9}>
              <Stack spacing="lg">
                <Group position="apart" className="nest-listing-toolbar">
                  <Group spacing="xs">
                    <ArrowsDownUp size={18} strokeWidth={1.5}/>
                    <Text weight={500} mr={theme.spacing.xs}>Sắp xếp</Text>
                    <RadioGroup
                      value={activeSort || ''}
                      onChange={(value) => updateActiveSort((value as '' | 'lowest-price' | 'highest-price') || null)}
                    >
                      <Radio value="" label="Mới nhất"/>
                      <Radio value="lowest-price" label="Giá thấp → cao"/>
                      <Radio value="highest-price" label="Giá cao → thấp"/>
                    </RadioGroup>
                  </Group>
                </Group>

                <ClientCategoryProducts categorySlug={category.categorySlug}/>
              </Stack>
            </Grid.Col>
          </Grid>

        </Stack>
      </Container>
    </main>
  );
}

function useGetCategoryApi(categorySlug: string) {
  const {
    data: categoryResponse,
    isLoading: isLoadingCategoryResponse,
    isError: isErrorCategoryResponse,
  } = useQuery<ClientCategoryResponse, ErrorMessage>(
    ['client-api', 'categories', 'getCategory', categorySlug],
    () => FetchUtils.get(ResourceURL.CLIENT_CATEGORY + '/' + categorySlug),
    {
      onError: () => NotifyUtils.simpleFailed('Lấy dữ liệu không thành công'),
      refetchOnWindowFocus: false,
      keepPreviousData: true,
    }
  );

  return { categoryResponse, isLoadingCategoryResponse, isErrorCategoryResponse };
}

function useGetFilterApi(categorySlug: string) {
  const {
    data: filterResponse,
    isLoading: isLoadingFilterResponse,
    isError: isErrorFilterResponse,
  } = useQuery<ClientFilterResponse, ErrorMessage>(
    ['client-api', 'filters', 'getFilterByCategorySlug', categorySlug],
    () => FetchUtils.get(ResourceURL.CLIENT_FILTER_CATEGORY, { slug: categorySlug }),
    {
      onError: () => NotifyUtils.simpleFailed('Lấy dữ liệu không thành công'),
      refetchOnWindowFocus: false,
      keepPreviousData: true,
    }
  );

  return { filterResponse, isLoadingFilterResponse, isErrorFilterResponse };
}


export default ClientCategory;
