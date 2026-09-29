import { Stack, Title } from '@mantine/core';
import NestProductCard from 'components/Nest/NestProductCard';
import React from 'react';
import { ClientProductResponse } from 'types';

interface ClientProductRelatedProductsProps {
  product: ClientProductResponse;
}

function ClientProductRelatedProducts({ product }: ClientProductRelatedProductsProps) {
  return (
    <Stack>
      <Title order={2}>Có thể bạn cũng thích</Title>
      <div className="nest-product-grid">
        {product.productRelatedProducts.map(product => <NestProductCard key={product.productId} product={product}/>)}
      </div>
    </Stack>
  );
}

export default ClientProductRelatedProducts;
