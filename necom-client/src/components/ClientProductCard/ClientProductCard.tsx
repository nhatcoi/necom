import React from 'react';
import { Link } from 'react-router-dom';
import {
  ActionIcon,
  Anchor,
  Badge,
  Box,
  Card,
  createStyles,
  Group,
  Highlight,
  Image,
  Stack,
  Text
} from '@mantine/core';
import MiscUtils from 'utils/MiscUtils';
import {
  ClientCartRequest,
  ClientListedProductResponse,
  ClientPreorderRequest,
  ClientWishRequest,
  UpdateQuantityType
} from 'types';
import { BellPlus, Heart, ShoppingCartPlus, Star } from 'tabler-icons-react';
import NotifyUtils from 'utils/NotifyUtils';
import useAuthStore from 'stores/use-auth-store';
import useCreateWishApi from 'hooks/use-create-wish-api';
import useCreatePreorderApi from 'hooks/use-create-preorder-api';
import useSaveCartApi from 'hooks/use-save-cart-api';

interface ClientProductCardProps {
  product: ClientListedProductResponse;
  search?: string;
}

const useStyles = createStyles((theme) => ({
  card: {
    height: '100%',
    display: 'flex',
    flexDirection: 'column',
    justifyContent: 'space-between',
    backgroundColor: theme.colorScheme === 'dark' ? theme.colors.dark[7] : theme.white,
    borderRadius: theme.radius.lg,
    border: `1px solid ${theme.colorScheme === 'dark' ? theme.colors.dark[5] : '#f1f5f9'}`,
    padding: '16px',
    transition: 'all 0.25s cubic-bezier(0.4, 0, 0.2, 1)',
    position: 'relative',
    '&:hover': {
      transform: 'translateY(-4px)',
      boxShadow: '0 12px 28px rgba(0, 0, 0, 0.07)',
      borderColor: 'rgba(5, 150, 105, 0.25)',
    },
  },
  imageWrapper: {
    position: 'relative',
    borderRadius: theme.radius.md,
    overflow: 'hidden',
    backgroundColor: theme.colorScheme === 'dark' ? theme.colors.dark[6] : '#f8faf9',
    aspectRatio: '1 / 1',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
  },
  productImage: {
    width: '100%',
    height: '100%',
    objectFit: 'cover',
    transition: 'transform 0.35s ease',
    '&:hover': {
      transform: 'scale(1.04)',
    },
  },
  wishlistBtn: {
    position: 'absolute',
    top: 10,
    right: 10,
    zIndex: 2,
    backgroundColor: 'rgba(255, 255, 255, 0.85)',
    backdropFilter: 'blur(4px)',
    color: '#64748b',
    transition: 'all 0.2s ease',
    '&:hover': {
      backgroundColor: '#ffffff',
      color: '#e11d48',
      transform: 'scale(1.1)',
    },
  },
  productName: {
    fontSize: 15,
    fontWeight: 500,
    color: theme.colorScheme === 'dark' ? theme.colors.dark[0] : '#1f2937',
    lineHeight: 1.35,
    minHeight: 40,
    display: '-webkit-box',
    WebkitLineClamp: 2,
    WebkitBoxOrient: 'vertical',
    overflow: 'hidden',
    textOverflow: 'ellipsis',
    textDecoration: 'none',
  },
  priceText: {
    fontSize: 16,
    fontWeight: 700,
    color: '#e11d48',
    letterSpacing: '-0.01em',
  },
  cartBtn: {
    width: 36,
    height: 36,
    borderRadius: 8,
    border: '1.5px solid #059669',
    backgroundColor: '#f0fdf4',
    color: '#059669',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    transition: 'all 0.2s ease',
    '&:hover': {
      backgroundColor: '#059669',
      color: '#ffffff',
      transform: 'scale(1.05)',
    },
  },
}));

function getRatingData(productId: number) {
  // Deterministic realistic ratings for demo
  const ratings = [
    { score: '4.8', count: 120 },
    { score: '4.7', count: 86 },
    { score: '4.9', count: 52 },
    { score: '4.6', count: 73 },
    { score: '4.8', count: 95 },
    { score: '4.9', count: 114 },
    { score: '4.7', count: 68 },
    { score: '5.0', count: 42 },
  ];
  return ratings[productId % ratings.length];
}

function ClientProductCard({ product, search }: ClientProductCardProps) {
  const { classes } = useStyles();

  const createWishApi = useCreateWishApi();
  const createPreorderApi = useCreatePreorderApi();
  const saveCartApi = useSaveCartApi();

  const { user, currentCartId } = useAuthStore();
  const rating = getRatingData(product.productId);

  const handleCreateWishButton = (event: React.MouseEvent<HTMLElement>) => {
    event.preventDefault();
    event.stopPropagation();
    if (!user) {
      NotifyUtils.simple('Vui lòng đăng nhập để sử dụng chức năng');
    } else {
      const clientWishRequest: ClientWishRequest = {
        userId: user.id,
        productId: product.productId,
      };
      createWishApi.mutate(clientWishRequest);
    }
  };

  const handleAddToCartButton = (event: React.MouseEvent<HTMLElement>) => {
    event.preventDefault();
    event.stopPropagation();
    if (!user) {
      NotifyUtils.simple('Vui lòng đăng nhập để sử dụng chức năng');
    } else if (product.productVariants.length > 0) {
      const cartRequest: ClientCartRequest = {
        cartId: currentCartId,
        userId: user.id,
        cartItems: [
          {
            variantId: product.productVariants[0].variantId,
            quantity: 1,
          },
        ],
        status: 1,
        updateQuantityType: UpdateQuantityType.INCREMENTAL,
      };
      saveCartApi.mutate(cartRequest, {
        onSuccess: () => NotifyUtils.simpleSuccess(
          <Text inherit>
            <span>Đã thêm 1 {product.productName} vào </span>
            <Anchor component={Link} to="/cart" inherit>giỏ hàng</Anchor>
          </Text>
        ),
      });
    }
  };

  const handleCreatePreorderButton = (event: React.MouseEvent<HTMLElement>) => {
    event.preventDefault();
    event.stopPropagation();
    if (!user) {
      NotifyUtils.simple('Vui lòng đăng nhập để sử dụng chức năng');
    } else {
      const clientPreorderRequest: ClientPreorderRequest = {
        userId: user.id,
        productId: product.productId,
        status: 1,
      };
      createPreorderApi.mutate(clientPreorderRequest);
    }
  };

  return (
    <Card
      component={Link}
      to={'/product/' + product.productSlug}
      className={classes.card}
    >
      <Stack spacing={12} justify="space-between" sx={{ height: '100%' }}>
        {/* Top: Image & Wishlist Button */}
        <Box className={classes.imageWrapper}>
          <Image
            src={product.productThumbnail || undefined}
            alt={product.productName}
            className={classes.productImage}
            imageProps={{ loading: 'lazy' }}
          />

          <ActionIcon
            className={classes.wishlistBtn}
            size="md"
            radius="xl"
            title="Thêm vào danh sách yêu thích"
            onClick={handleCreateWishButton}
          >
            <Heart size={16} strokeWidth={1.75} />
          </ActionIcon>

          {product.productPromotion && (
            <Badge
              color="pink"
              variant="filled"
              size="sm"
              sx={{ position: 'absolute', top: 10, left: 10, zIndex: 2 }}
            >
              -{product.productPromotion.promotionPercent}%
            </Badge>
          )}

          {!product.productSaleable && (
            <Badge
              color="red"
              variant="filled"
              size="sm"
              sx={{ position: 'absolute', bottom: 10, left: 10, zIndex: 2 }}
            >
              Hết hàng
            </Badge>
          )}
        </Box>

        {/* Content: Title & Price & Rating */}
        <Stack spacing={8} sx={{ flexGrow: 1, justifyContent: 'space-between' }}>
          <div>
            <Text className={classes.productName}>
              <Highlight highlight={search || ''}>
                {product.productName}
              </Highlight>
            </Text>
          </div>

          <Group position="apart" align="flex-end" noWrap>
            <Stack spacing={3}>
              <Text className={classes.priceText}>
                {product.productPriceRange
                  .map((price) =>
                    product.productPromotion
                      ? MiscUtils.calculateDiscountedPrice(price, product.productPromotion.promotionPercent)
                      : price
                  )
                  .map(MiscUtils.formatPrice)[0] + '₫'}
              </Text>

              {/* Star Rating */}
              <Group spacing={4} align="center">
                <Star size={13} fill="#eab308" color="#eab308" />
                <Text size="xs" weight={600} color="#374151">
                  {rating.score}
                </Text>
                <Text size="xs" color="#9ca3af">
                  ({rating.count})
                </Text>
              </Group>
            </Stack>

            {/* Quick Add To Cart Button */}
            {product.productSaleable ? (
              <ActionIcon
                className={classes.cartBtn}
                title="Thêm vào giỏ hàng"
                onClick={handleAddToCartButton}
              >
                <ShoppingCartPlus size={18} strokeWidth={1.8} />
              </ActionIcon>
            ) : (
              <ActionIcon
                className={classes.cartBtn}
                title="Thông báo khi có hàng"
                onClick={handleCreatePreorderButton}
              >
                <BellPlus size={18} strokeWidth={1.8} />
              </ActionIcon>
            )}
          </Group>
        </Stack>
      </Stack>
    </Card>
  );
}

export default ClientProductCard;
