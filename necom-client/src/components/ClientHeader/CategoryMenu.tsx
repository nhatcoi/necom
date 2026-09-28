import React, { Dispatch, SetStateAction } from 'react';
import {
  Anchor,
  Box,
  Button,
  Grid,
  Group,
  Paper,
  Skeleton,
  Stack,
  Text,
  ThemeIcon,
  UnstyledButton,
  useMantineTheme
} from '@mantine/core';
import { useNavigate } from 'react-router-dom';
import PageConfigs from 'pages/PageConfigs';
import { useQuery } from 'react-query';
import { ClientCategoryResponse, CollectionWrapper } from 'types';
import FetchUtils, { ErrorMessage } from 'utils/FetchUtils';
import ResourceURL from 'constants/ResourceURL';
import NotifyUtils from 'utils/NotifyUtils';
import { AlertTriangle, ArrowRight, List } from 'tabler-icons-react';

const CATEGORY_SUBTITLES: Record<string, string> = {
  'ban-ghe': 'Bàn ăn, ghế tựa, đôn gỗ tối giản',
  'sofa': 'Sofa văng bọc nỉ, sofa góc thư giãn',
  'tu-ke': 'Kệ sách, kệ để giày, tủ lưu trữ',
  'den-chieu-sang': 'Đèn thả trần Japandi, đèn ngủ gốm',
  'decor': 'Bình hoa mộc, gối tựa, tranh tối giản',
  'do-bep': 'Nồi gang tráng men, bộ cốc thủ công',
  'do-luu-tru': 'Hộp vải Muji, khay sắp xếp đa năng',
  'van-phong-tai-nha': 'Bàn làm việc gỗ sồi, kệ màn hình',
  'cay-canh': 'Cây bàng Singapore, chậu gốm để bàn',
};

function CategoryMenu({ setOpenedCategoryMenu }: { setOpenedCategoryMenu: Dispatch<SetStateAction<boolean>> }) {
  const theme = useMantineTheme();
  const navigate = useNavigate();

  const {
    data: categoryResponses,
    isLoading: isLoadingCategoryResponses,
    isError: isErrorCategoryResponses,
  } = useQuery<CollectionWrapper<ClientCategoryResponse>, ErrorMessage>(
    ['client-api', 'categories', 'getAllCategories'],
    () => FetchUtils.get(ResourceURL.CLIENT_CATEGORY),
    {
      onError: () => NotifyUtils.simpleFailed('Lấy dữ liệu không thành công'),
      refetchOnWindowFocus: false,
      keepPreviousData: true,
    }
  );

  if (isLoadingCategoryResponses) {
    return (
      <Box p="md">
        <Grid>
          {Array(9).fill(0).map((_, index) => (
            <Grid.Col span={4} key={index}>
              <Skeleton height={68} radius="md" />
            </Grid.Col>
          ))}
        </Grid>
      </Box>
    );
  }

  if (isErrorCategoryResponses) {
    return (
      <Stack my={theme.spacing.xl} sx={{ alignItems: 'center', color: theme.colors.pink[6] }}>
        <AlertTriangle size={80} strokeWidth={1} />
        <Text size="md" weight={500}>Đã có lỗi khi tải danh mục</Text>
      </Stack>
    );
  }

  const handleNavigate = (path: string) => {
    setOpenedCategoryMenu(false);
    setTimeout(() => navigate(path), 150);
  };

  const categories = categoryResponses?.content || [];

  return (
    <Box p="md" sx={{ minWidth: 680, maxWidth: 880 }}>
      {/* Header */}
      <Group position="apart" mb="sm" pb="xs" sx={{ borderBottom: `1px solid ${theme.colorScheme === 'dark' ? theme.colors.dark[5] : theme.colors.gray[2]}` }}>
        <Group spacing="xs">
          <ThemeIcon color="green" size="md" radius="md" variant="light">
            <List size={16} />
          </ThemeIcon>
          <div>
            <Text weight={700} size="sm" color={theme.colorScheme === 'dark' ? theme.white : '#1f2937'}>
              Danh mục Home &amp; Living
            </Text>
            <Text size="xs" color="dimmed">
              Lựa chọn các sản phẩm tinh tuyển theo từng không gian tổ ấm
            </Text>
          </div>
        </Group>

        <Anchor
          size="xs"
          weight={600}
          color="green"
          onClick={() => handleNavigate('/all-categories')}
          sx={{ display: 'flex', alignItems: 'center', gap: 4, cursor: 'pointer' }}
        >
          <span>Xem tất cả danh mục</span>
          <ArrowRight size={14} />
        </Anchor>
      </Group>

      {/* Grid of 9 Categories */}
      <Grid gutter="sm">
        {categories.map((cat, index) => {
          const CategoryIcon = PageConfigs.categorySlugIconMap[cat.categorySlug];
          const subText = CATEGORY_SUBTITLES[cat.categorySlug] || 'Khám phá sản phẩm';

          return (
            <Grid.Col span={4} key={index}>
              <UnstyledButton
                onClick={() => handleNavigate('/category/' + cat.categorySlug)}
                sx={{
                  width: '100%',
                  display: 'flex',
                  alignItems: 'center',
                  padding: '10px 12px',
                  borderRadius: theme.radius.md,
                  border: `1px solid ${theme.colorScheme === 'dark' ? theme.colors.dark[5] : 'rgba(0,0,0,0.04)'}`,
                  backgroundColor: theme.colorScheme === 'dark' ? theme.colors.dark[6] : '#fcfdfc',
                  transition: 'all 0.2s ease',
                  '&:hover': {
                    backgroundColor: theme.colorScheme === 'dark' ? theme.colors.dark[5] : '#edf5ed',
                    borderColor: 'rgba(5, 150, 105, 0.3)',
                    transform: 'translateY(-2px)',
                    boxShadow: '0 4px 12px rgba(5, 150, 105, 0.08)',
                  },
                }}
              >
                <ThemeIcon
                  size={38}
                  radius="md"
                  variant="light"
                  color="green"
                  mr="sm"
                  sx={{ flexShrink: 0 }}
                >
                  <CategoryIcon size={20} />
                </ThemeIcon>
                <div style={{ overflow: 'hidden' }}>
                  <Text
                    weight={600}
                    size="sm"
                    color={theme.colorScheme === 'dark' ? theme.white : '#1f2937'}
                    lineClamp={1}
                  >
                    {cat.categoryName}
                  </Text>
                  <Text size="xs" color="dimmed" lineClamp={1}>
                    {subText}
                  </Text>
                </div>
              </UnstyledButton>
            </Grid.Col>
          );
        })}
      </Grid>

      {/* Footer Banner */}
      <Paper
        mt="md"
        p="xs"
        radius="md"
        sx={{
          backgroundColor: theme.colorScheme === 'dark' ? theme.colors.dark[7] : '#f4f7f4',
          border: `1px dashed ${theme.colorScheme === 'dark' ? theme.colors.dark[4] : theme.colors.green[2]}`,
        }}
      >
        <Group position="apart">
          <Text size="xs" color="dimmed">
            🌿 Necom cam kết bảo hành kết cấu 12 - 24 tháng cho mọi sản phẩm đồ gỗ &amp; đèn chiếu sáng.
          </Text>
          <Button
            size="xs"
            variant="subtle"
            color="green"
            compact
            rightIcon={<ArrowRight size={12} />}
            onClick={() => handleNavigate('/all-categories')}
          >
            Tất cả danh mục
          </Button>
        </Group>
      </Paper>
    </Box>
  );
}

export default CategoryMenu;
