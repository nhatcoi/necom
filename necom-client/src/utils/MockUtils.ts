import { ClientCategoryResponse, ClientListedProductResponse } from 'types';
import { MessageResponse } from 'models/Message';

class MockUtils {
  static featuredCategories: ClientCategoryResponse[] = [
    {
      categoryName: 'Bàn ghế',
      categorySlug: 'ban-ghe',
      categoryChildren: [],
    },
    {
      categoryName: 'Sofa',
      categorySlug: 'sofa',
      categoryChildren: [],
    },
    {
      categoryName: 'Tủ kệ',
      categorySlug: 'tu-ke',
      categoryChildren: [],
    },
    {
      categoryName: 'Đèn & chiếu sáng',
      categorySlug: 'den-chieu-sang',
      categoryChildren: [],
    },
    {
      categoryName: 'Đồ decor',
      categorySlug: 'decor',
      categoryChildren: [],
    },
    {
      categoryName: 'Đồ bếp & ăn uống',
      categorySlug: 'do-bep',
      categoryChildren: [],
    },
    {
      categoryName: 'Đồ lưu trữ',
      categorySlug: 'do-luu-tru',
      categoryChildren: [],
    },
    {
      categoryName: 'Văn phòng tại nhà',
      categorySlug: 'van-phong-tai-nha',
      categoryChildren: [],
    },
  ];

  static allCategories: ClientCategoryResponse[] = [
    {
      categoryName: "Bàn ghế",
      categorySlug: "ban-ghe",
      categoryChildren: [
        { categoryName: "Bàn ăn", categorySlug: "ban-an", categoryChildren: [] },
        { categoryName: "Ghế ăn", categorySlug: "ghe-an", categoryChildren: [] },
        { categoryName: "Bàn trà", categorySlug: "ban-tra", categoryChildren: [] },
        { categoryName: "Ghế thư giãn", categorySlug: "ghe-thu-gian", categoryChildren: [] },
      ],
    },
    {
      categoryName: "Sofa",
      categorySlug: "sofa",
      categoryChildren: [
        { categoryName: "Sofa văng", categorySlug: "sofa-vang", categoryChildren: [] },
        { categoryName: "Sofa góc", categorySlug: "sofa-goc", categoryChildren: [] },
        { categoryName: "Ghế đôn", categorySlug: "ghe-don", categoryChildren: [] },
      ],
    },
    {
      categoryName: "Tủ kệ",
      categorySlug: "tu-ke",
      categoryChildren: [
        { categoryName: "Kệ sách", categorySlug: "ke-sach", categoryChildren: [] },
        { categoryName: "Tủ giày", categorySlug: "tu-giay", categoryChildren: [] },
        { categoryName: "Kệ tivi", categorySlug: "ke-tivi", categoryChildren: [] },
      ],
    },
    {
      categoryName: "Đèn & chiếu sáng",
      categorySlug: "den-chieu-sang",
      categoryChildren: [
        { categoryName: "Đèn thả trần", categorySlug: "den-tha-tran", categoryChildren: [] },
        { categoryName: "Đèn để bàn", categorySlug: "den-de-ban", categoryChildren: [] },
        { categoryName: "Đèn cây đứng", categorySlug: "den-cay-dung", categoryChildren: [] },
      ],
    },
    {
      categoryName: "Đồ decor",
      categorySlug: "decor",
      categoryChildren: [
        { categoryName: "Bình hoa gốm", categorySlug: "binh-hoa-gom", categoryChildren: [] },
        { categoryName: "Tranh treo tường", categorySlug: "tranh-treo-tuong", categoryChildren: [] },
        { categoryName: "Đồng hồ decor", categorySlug: "dong-ho-decor", categoryChildren: [] },
      ],
    },
    {
      categoryName: "Đồ bếp & ăn uống",
      categorySlug: "do-bep",
      categoryChildren: [
        { categoryName: "Nồi chảo gang", categorySlug: "noi-chao-gang", categoryChildren: [] },
        { categoryName: "Bộ bát đĩa gốm", categorySlug: "bo-bat-dia-gom", categoryChildren: [] },
        { categoryName: "Cốc ly tách", categorySlug: "coc-ly-tach", categoryChildren: [] },
      ],
    },
    {
      categoryName: "Đồ lưu trữ",
      categorySlug: "do-luu-tru",
      categoryChildren: [
        { categoryName: "Hộp vải lưu trữ", categorySlug: "hop-vai-luu-tru", categoryChildren: [] },
        { categoryName: "Giỏ mây tre đan", categorySlug: "gio-may-tre", categoryChildren: [] },
      ],
    },
    {
      categoryName: "Văn phòng tại nhà",
      categorySlug: "van-phong-tai-nha",
      categoryChildren: [
        { categoryName: "Bàn làm việc", categorySlug: "ban-lam-viec", categoryChildren: [] },
        { categoryName: "Kệ nâng màn hình", categorySlug: "ke-nang-man-hinh", categoryChildren: [] },
      ],
    },
    {
      categoryName: "Cây xanh trang trí",
      categorySlug: "cay-canh",
      categoryChildren: [
        { categoryName: "Cây lọc không khí", categorySlug: "cay-loc-khong-khi", categoryChildren: [] },
        { categoryName: "Cây để bàn", categorySlug: "cay-de-ban", categoryChildren: [] },
      ],
    },
  ];

  static sampleCategory: ClientCategoryResponse = {
    categoryName: 'MacBook',
    categorySlug: 'laptop-macbook',
    categoryChildren: [
      {
        categoryName: 'MacBook Air',
        categorySlug: 'laptop-macbook-air',
        categoryChildren: [],
      },
    ],
    categoryParent: {
      categoryName: 'Apple',
      categorySlug: 'laptop-apple',
      categoryChildren: [],
      categoryParent: {
        categoryName: 'Laptop',
        categorySlug: 'laptop',
        categoryChildren: [],
      },
    },
  };

  static sampleProduct: ClientListedProductResponse = {
    productId: 1,
    productName: 'Lenovo Legion 5 Pro 2022',
    productSlug: 'lenovo-legion-5-pro-2022',
    productThumbnail: 'https://dummyimage.com/400x400/e8e8e8/6e6e6e.png',
    productPriceRange: [10_000_000, 12_000_000],
    productVariants: [],
    productSaleable: true,
    productPromotion: {
      promotionId: 1,
      promotionPercent: 10,
    },
  };

  static sampleMessages: MessageResponse[] = [
    {
      id: 2,
      createdAt: '',
      updatedAt: '',
      content: 'This is a content',
      status: 1,
      user: {
        id: 1,
        username: 'dtreat3',
        fullname: 'Admin',
        email: '',
      },
      roomId: 1,
      type: 'TEXT',
      senderType: 'AGENT',
      payload: null,
      clientMsgId: null,
    },
    {
      id: 1,
      createdAt: '',
      updatedAt: '',
      content: 'This is a content',
      status: 1,
      user: {
        id: 4,
        username: 'dtreat3',
        fullname: 'Daniel',
        email: '',
      },
      roomId: 1,
      type: 'TEXT',
      senderType: 'CUSTOMER',
      payload: null,
      clientMsgId: null,
    },
  ];
}

export default MockUtils;
