import React from 'react';
import { fireEvent, render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { useQuery } from 'react-query';
import ClientHome from './ClientHome';

jest.mock('react-query', () => ({ useQuery: jest.fn() }));
jest.mock('hooks/use-title', () => ({ __esModule: true, default: jest.fn() }));
jest.mock('components/Nest/NestProductCard', () => ({ __esModule: true, default: () => null, productPrice: () => '100₫' }));
const retry = jest.fn();
beforeEach(() => { jest.clearAllMocks(); });
function mount() { render(<MemoryRouter><ClientHome/></MemoryRouter>); }

test('keeps discovery usable and offers retry when products cannot load', () => {
  (useQuery as jest.Mock).mockReturnValue({ isError: true, refetch: retry });
  mount();
  expect(screen.getByRole('heading', { level: 1 })).toHaveTextContent('Nhà,');
  fireEvent.click(screen.getByRole('button', { name: 'Thử lại' }));
  expect(retry).toHaveBeenCalledTimes(1);
  expect(screen.getByRole('link', { name: /02 Một chiếc bàn vừa xinh/ })).toHaveAttribute('href', '/search?q=b%C3%A0n%20tr%C3%A0');
});
test('renders an empty state instead of invented product data', () => {
  (useQuery as jest.Mock).mockImplementation(([scope]) => scope === 'nest' ? { data: [] } : { data: { content: [], totalElements: 0 } });
  mount();
  expect(screen.getByText('Bộ sưu tập đang được cập nhật.')).toBeInTheDocument();
  expect(screen.getByRole('link', { name: /Khám phá các danh mục/ })).toHaveAttribute('href', '/all-categories');
});
test('room hotspots work as exclusive selections', () => {
  (useQuery as jest.Mock).mockReturnValue({ isLoading: true });
  mount();
  fireEvent.click(screen.getByRole('button', { name: 'Gợi ý một chiếc bàn vừa xinh' }));
  expect(screen.getByRole('button', { name: 'Gợi ý một chiếc bàn vừa xinh' })).toHaveAttribute('aria-pressed', 'true');
  expect(screen.getByRole('button', { name: 'Gợi ý một chiếc sofa êm' })).toHaveAttribute('aria-pressed', 'false');
});
