import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { ArrowUpRight, Heart, Plus } from 'tabler-icons-react';
import { ClientListedProductResponse, UpdateQuantityType } from 'types';
import useWishlist from 'hooks/use-wishlist';
import useSaveCartApi from 'hooks/use-save-cart-api';
import useAuthStore from 'stores/use-auth-store';
import NotifyUtils from 'utils/NotifyUtils';
import MiscUtils from 'utils/MiscUtils';

export function productPrice(product: ClientListedProductResponse) {
  const prices = product.productPriceRange.filter(Number.isFinite);
  if (!prices.length) return 'Liên hệ';
  const price = Math.min(...prices);
  const discounted = product.productPromotion
    ? MiscUtils.calculateDiscountedPrice(price, product.productPromotion.promotionPercent) : price;
  return `${prices.length > 1 ? 'Từ ' : ''}${MiscUtils.formatPrice(discounted)}₫`;
}

export default function NestProductCard({ product }: { product: ClientListedProductResponse }) {
  const { isWished, toggleWish, isLoading: wishing } = useWishlist();
  const saveCart = useSaveCartApi();
  const { user, currentCartId } = useAuthStore();
  const navigate = useNavigate();
  const href = `/product/${product.productSlug}`;
  const needsSelection = product.productVariants.length !== 1 || !product.productSaleable;

  const addToCart = () => {
    if (needsSelection) { navigate(href); return; }
    if (!user) { NotifyUtils.simple('Vui lòng đăng nhập để thêm sản phẩm vào giỏ hàng'); return; }
    saveCart.mutate({
      cartId: currentCartId, userId: user.id, status: 1,
      cartItems: [{ variantId: product.productVariants[0].variantId, quantity: 1 }],
      updateQuantityType: UpdateQuantityType.INCREMENTAL,
    }, { onSuccess: () => NotifyUtils.simpleSuccess(`Đã thêm ${product.productName} vào giỏ hàng`) });
  };

  return (
    <article className="nest-product">
      <div className="nest-product-image">
        <Link to={href} tabIndex={-1} aria-hidden="true">
          <img src={product.productThumbnail || '/images/placeholder-product.svg'} alt="" loading="lazy"
            onError={(event) => { if (event.currentTarget.getAttribute('src') !== '/images/placeholder-product.svg') event.currentTarget.src = '/images/placeholder-product.svg'; }}/>
        </Link>
        <button className="nest-icon nest-product-wish" type="button" disabled={wishing}
          aria-label={`${isWished(product.productId) ? 'Bỏ yêu thích' : 'Yêu thích'} ${product.productName}`}
          aria-pressed={isWished(product.productId)} onClick={() => toggleWish(product)}>
          <Heart size={19} strokeWidth={1.4} fill={isWished(product.productId) ? 'currentColor' : 'none'}/>
        </button>
        {product.productPromotion && <span className="nest-product-badge">−{product.productPromotion.promotionPercent}%</span>}
        {!product.productSaleable && <span className="nest-product-badge">Tạm hết hàng</span>}
      </div>
      <div className="nest-product-info">
        <p className="nest-product-options">{product.productVariants.length > 1 ? `${product.productVariants.length} lựa chọn` : 'Khám phá chi tiết'}</p>
        <h3><Link to={href}>{product.productName}</Link></h3>
        <div className="nest-product-bottom">
          <span>{productPrice(product)}</span>
          <button className="nest-icon nest-product-add" type="button" onClick={addToCart} disabled={saveCart.isLoading}
            aria-label={`${needsSelection ? 'Xem lựa chọn cho' : 'Thêm vào giỏ hàng:'} ${product.productName}`}>
            {needsSelection ? <ArrowUpRight size={19}/> : <Plus size={19}/>}
          </button>
        </div>
      </div>
    </article>
  );
}
