import React from 'react';
import { useMutation, useQuery, useQueryClient } from 'react-query';
import { ClientWishRequest, ClientWishResponse } from 'types';
import FetchUtils, { ErrorMessage, ListResponse } from 'utils/FetchUtils';
import ResourceURL from 'constants/ResourceURL';
import NotifyUtils from 'utils/NotifyUtils';
import useAuthStore from 'stores/use-auth-store';
import { Anchor, Text } from '@mantine/core';
import { Link } from 'react-router-dom';

export function useWishlist() {
  const { user } = useAuthStore();
  const queryClient = useQueryClient();

  const { data: wishData } = useQuery<ListResponse<ClientWishResponse>, ErrorMessage>(
    ['client-api', 'wishes', 'userWishes'],
    () => FetchUtils.getWithToken(ResourceURL.CLIENT_WISH, { page: 1, size: 200 }),
    {
      enabled: !!user,
      staleTime: 1000 * 60 * 5,
      refetchOnWindowFocus: false,
    }
  );

  const wishes = React.useMemo(() => wishData?.content || [], [wishData?.content]);
  const totalWishes = wishData?.totalElements ?? wishes.length;

  const wishMap = React.useMemo(() => {
    const map = new Map<number, number>(); // productId -> wishId
    wishes.forEach((w) => {
      if (w.wishProduct?.productId) {
        map.set(w.wishProduct.productId, w.wishId);
      }
    });
    return map;
  }, [wishes]);

  const isWished = React.useCallback(
    (productId: number) => wishMap.has(productId),
    [wishMap]
  );

  const createWishMutation = useMutation<ClientWishResponse, ErrorMessage, ClientWishRequest>(
    (body) => FetchUtils.postWithToken(ResourceURL.CLIENT_WISH, body),
    {
      onSuccess: (response) => {
        void queryClient.invalidateQueries(['client-api', 'wishes']);
        NotifyUtils.simpleSuccess(
          <Text inherit>
            <span>Đã thêm sản phẩm {response.wishProduct.productName} vào </span>
            <Anchor component={Link} to="/user/wishlist" inherit>danh sách yêu thích</Anchor>
          </Text>
        );
      },
      onError: () => NotifyUtils.simpleFailed('Không thêm được sản phẩm vào danh sách yêu thích'),
    }
  );

  const deleteWishMutation = useMutation<void, ErrorMessage, number[]>(
    (wishIds) => FetchUtils.deleteWithToken(ResourceURL.CLIENT_WISH, wishIds),
    {
      onSuccess: () => {
        void queryClient.invalidateQueries(['client-api', 'wishes']);
        NotifyUtils.simpleSuccess('Đã xóa sản phẩm khỏi danh sách yêu thích');
      },
      onError: () => NotifyUtils.simpleFailed('Xóa sản phẩm khỏi danh sách yêu thích thất bại'),
    }
  );

  const toggleWish = React.useCallback(
    (product: { productId: number; productName?: string }) => {
      if (!user) {
        NotifyUtils.simple('Vui lòng đăng nhập để sử dụng chức năng');
        return;
      }
      const wishId = wishMap.get(product.productId);
      if (wishId) {
        deleteWishMutation.mutate([wishId]);
      } else {
        createWishMutation.mutate({
          userId: user.id,
          productId: product.productId,
        });
      }
    },
    [user, wishMap, createWishMutation, deleteWishMutation]
  );

  return {
    wishes,
    totalWishes,
    isWished,
    toggleWish,
    isLoading: createWishMutation.isLoading || deleteWishMutation.isLoading,
  };
}

export default useWishlist;
