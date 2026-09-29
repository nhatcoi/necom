import React from 'react';
import { fireEvent, render, screen } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import NestProductCard, { productPrice } from './NestProductCard';
import { ClientListedProductResponse } from 'types';
import useAuthStore from 'stores/use-auth-store';
import useSaveCartApi from 'hooks/use-save-cart-api';
import useWishlist from 'hooks/use-wishlist';
import NotifyUtils from 'utils/NotifyUtils';

jest.mock('stores/use-auth-store');
jest.mock('hooks/use-save-cart-api');
jest.mock('hooks/use-wishlist');
jest.mock('utils/NotifyUtils', () => ({ __esModule: true, default: { simple: jest.fn(), simpleSuccess: jest.fn() } }));

const product: ClientListedProductResponse = {
  productId: 3, productName: 'Sofa linen', productSlug: 'sofa-linen', productThumbnail: null,
  productPriceRange: [2000000, 1000000], productVariants: [{ variantId: 5, variantPrice: 1000000, variantProperties: null }],
  productSaleable: true, productPromotion: { promotionId: 1, promotionPercent: 10 },
};
const mutate = jest.fn();
const wish = jest.fn();
function mount(item = product) {
  render(<MemoryRouter><Routes><Route path="/" element={<NestProductCard product={item}/>}/><Route path="/product/sofa-linen" element={<p>Chọn phiên bản sofa</p>}/></Routes></MemoryRouter>);
}
beforeEach(() => {
  jest.clearAllMocks();
  (useAuthStore as unknown as jest.Mock).mockReturnValue({ user: { id: 2 }, currentCartId: 7 });
  (useSaveCartApi as jest.Mock).mockReturnValue({ mutate, isLoading: false });
  (useWishlist as jest.Mock).mockReturnValue({ isWished: () => false, toggleWish: wish, isLoading: false });
});

test('shows the lowest discounted price regardless of API price order', () => {
  expect(productPrice(product)).toBe('Từ 900.000₫');
  expect(productPrice({ ...product, productPriceRange: [] })).toBe('Liên hệ');
});
test('requires choosing a variant instead of silently adding the first option', () => {
  mount({ ...product, productVariants: [...product.productVariants, { variantId: 6, variantPrice: 2000000, variantProperties: null }] });
  fireEvent.click(screen.getByRole('button', { name: 'Xem lựa chọn cho Sofa linen' }));
  expect(screen.getByText('Chọn phiên bản sofa')).toBeInTheDocument();
  expect(mutate).not.toHaveBeenCalled();
});
test('adds a single variant using the existing authenticated cart contract', () => {
  mount();
  fireEvent.click(screen.getByRole('button', { name: 'Thêm vào giỏ hàng: Sofa linen' }));
  expect(mutate).toHaveBeenCalledWith(expect.objectContaining({ cartId: 7, userId: 2, cartItems: [{ variantId: 5, quantity: 1 }], updateQuantityType: 'INCREMENTAL' }), expect.any(Object));
});
test('does not mutate the cart for a signed-out visitor', () => {
  (useAuthStore as unknown as jest.Mock).mockReturnValue({ user: null, currentCartId: null });
  mount();
  fireEvent.click(screen.getByRole('button', { name: 'Thêm vào giỏ hàng: Sofa linen' }));
  expect(mutate).not.toHaveBeenCalled();
  expect(NotifyUtils.simple).toHaveBeenCalled();
});
test('wishlist is a separate action and does not navigate or add to cart', () => {
  mount();
  fireEvent.click(screen.getByRole('button', { name: 'Yêu thích Sofa linen' }));
  expect(wish).toHaveBeenCalledWith(product);
  expect(mutate).not.toHaveBeenCalled();
  expect(screen.queryByText('Chọn phiên bản sofa')).not.toBeInTheDocument();
});
