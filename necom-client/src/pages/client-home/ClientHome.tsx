import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { Modal } from '@mantine/core';
import { ArrowDown, ArrowRight, ArrowUpRight, Headset, Leaf, Package, TruckDelivery } from 'tabler-icons-react';
import { useQuery } from 'react-query';
import useTitle from 'hooks/use-title';
import FetchUtils, { ListResponse } from 'utils/FetchUtils';
import ResourceURL from 'constants/ResourceURL';
import { ClientListedProductResponse } from 'types';
import NestProductCard, { productPrice } from 'components/Nest/NestProductCard';
import { journals, nestImages, rooms } from './nest-content';

function SectionHeading({ eyebrow, title, href, action }: { eyebrow: string; title: string; href?: string; action?: string }) {
  return <div className="nest-section-heading"><div><p className="nest-eyebrow">{eyebrow}</p><h2>{title}</h2></div>
    {href && <Link className="nest-text-link" to={href}>{action || 'Khám phá tất cả'} <ArrowUpRight size={17}/></Link>}</div>;
}

function FeaturedProducts() {
  const [selection, setSelection] = useState('selected');
  const params = { size: 4, newable: true, saleable: true, ...(selection === 'lighting' ? { search: 'đèn' } : {}) };
  const { data, isLoading, isError, refetch } = useQuery<ListResponse<ClientListedProductResponse>>(
    ['client-api', 'products', 'getAllProducts', params], () => FetchUtils.get(ResourceURL.CLIENT_PRODUCT, params),
    { refetchOnWindowFocus: false, staleTime: 60000, retry: 1 }
  );
  return <section className="nest-section nest-container" id="products" aria-labelledby="nest-products-title">
    <div className="nest-section-heading"><div><p className="nest-eyebrow">NHỮNG MÓN ĐỒ CHO NGÀY THƯỜNG THÊM ĐẸP</p><h2 id="nest-products-title">Một chút mới cho tổ ấm.</h2></div>
      <Link to="/search" className="nest-text-link">Xem tất cả sản phẩm <ArrowUpRight size={17}/></Link></div>
    <div className="nest-product-tabs" role="group" aria-label="Chọn nhóm sản phẩm">
      <button type="button" aria-pressed={selection === 'selected'} onClick={() => setSelection('selected')}>Khám phá cùng Nest</button>
      <button type="button" aria-pressed={selection === 'lighting'} onClick={() => setSelection('lighting')}>Đèn & ánh sáng</button>
    </div>
    {isLoading ? <div className="nest-product-grid" aria-label="Đang tải sản phẩm" aria-busy="true">{[1, 2, 3, 4].map((i) => <div className="nest-product-skeleton" key={i}/>)}</div>
      : isError ? <div className="nest-empty" role="status"><p>Chưa thể tải sản phẩm. Bạn thử lại nhé.</p><button type="button" className="nest-button" onClick={() => refetch()}>Thử lại <ArrowRight size={16}/></button></div>
        : !data?.content.length ? <div className="nest-empty"><p>Bộ sưu tập đang được cập nhật.</p><Link className="nest-text-link" to="/all-categories">Khám phá các danh mục <ArrowRight size={16}/></Link></div>
          : <div className="nest-product-grid">{data.content.map((product) => <NestProductCard key={product.productId} product={product}/>)}</div>}
  </section>;
}

const sceneItems = [
  { name: 'Một chiếc sofa êm', search: 'sofa', href: '/category/sofa', className: 'sofa' },
  { name: 'Một chiếc bàn vừa xinh', search: 'bàn trà', href: '/search?q=b%C3%A0n%20tr%C3%A0', className: 'table' },
  { name: 'Một điểm sáng ấm áp', search: 'đèn', href: '/category/den-chieu-sang', className: 'lamp' },
];

function ShopTheRoom() {
  const [active, setActive] = useState(0);
  const { data, isLoading, isError, refetch } = useQuery<(ClientListedProductResponse | undefined)[]>(
    ['nest', 'room-suggestions', { size: 12, newable: true }],
  () => Promise.all(sceneItems.map(async (item) => {
    const result = await FetchUtils.get(ResourceURL.CLIENT_PRODUCT, { size: 12, search: item.search, saleable: true, newable: true }) as ListResponse<ClientListedProductResponse>;
    return result.content.find((product) => product.productName.normalize('NFC').toLowerCase().includes(item.search));
  })), { refetchOnWindowFocus: false, staleTime: 300000, retry: 1 }
  );
  return <section className="nest-room-section" aria-labelledby="nest-room-title">
    <div className="nest-room-photo">
      <img src={nestImages.living} alt="Phối cảnh phòng khách với sofa vải kem, bàn trà gỗ và đèn thả" loading="lazy"/>
      <div className="nest-room-overlay"><p className="nest-eyebrow">PHỐI MỘT GÓC NHÀ</p><h2 id="nest-room-title">Một góc nhà,<br/>trọn cảm hứng.</h2></div>
      {sceneItems.map((item, index) => <button key={item.className} type="button" className={`nest-hotspot nest-hotspot-${item.className}`} aria-label={`Gợi ý ${item.name.toLowerCase()}`} aria-pressed={active === index} aria-controls="nest-room-list" onClick={() => setActive(index)}>{index + 1}</button>)}
      <span className="nest-scene-caption">Ảnh phối cảnh gợi ý · Khám phá các sản phẩm tương tự</span>
    </div>
    <div className="nest-room-products"><p className="nest-eyebrow">MANG CẢM HỨNG VỀ NHÀ</p><h3>Những món đồ<br/>làm nên không gian.</h3>
      <p className="nest-muted">Chọn một điểm trên ảnh để khám phá sản phẩm tương tự, rồi phối theo cách riêng của bạn.</p>
      <div id="nest-room-list" className="nest-room-list">
        {sceneItems.map((item, index) => {
          const product = data?.[index];
          return <Link key={item.className} to={product ? `/product/${product.productSlug}` : item.href} className={`nest-room-item ${active === index ? 'is-active' : ''}`} onFocus={() => setActive(index)}>
            <span className="nest-room-number">0{index + 1}</span>
            {product?.productThumbnail && <img src={product.productThumbnail} alt="" loading="lazy"/>}
            <div><h4>{product?.productName || item.name}</h4><p>{product ? productPrice(product) : isLoading ? 'Đang tìm gợi ý…' : 'Khám phá danh mục'}</p></div><ArrowUpRight size={17}/>
          </Link>;
        })}
      </div>
      {isError && <button type="button" className="nest-text-link" onClick={() => refetch()}>Tải lại gợi ý sản phẩm <ArrowRight size={16}/></button>}
      <Link to="/category/sofa" className="nest-button">Bắt đầu từ phòng khách <ArrowRight size={17}/></Link>
    </div>
  </section>;
}

function ClientHome() {
  useTitle('Nest — Nhà, theo cách bạn yêu');
  const [article, setArticle] = useState<number | null>(null);
  const selectedArticle = article === null ? null : journals[article];

  return <main className="nest-home">
    <section className="nest-hero" aria-labelledby="nest-hero-title">
      <div className="nest-hero-copy"><p className="nest-eyebrow">NỘI THẤT & CẢM HỨNG SỐNG</p><h1 id="nest-hero-title">Nhà,<br/>theo cách<br/>bạn yêu<span>.</span></h1><p>Những món đồ giản dị, những chất liệu ấm áp.<br className="nest-desktop-break"/> Cùng bạn tạo nên một nơi luôn muốn trở về.</p>
        <Link className="nest-button" to="/#collections">Khám phá bộ sưu tập <ArrowRight size={18}/></Link>
        <Link className="nest-hero-scroll" to="/#spaces"><span className="nest-circle"><ArrowDown size={16}/></span> Tìm cảm hứng cho tổ ấm</Link>
      </div>
      <div className="nest-hero-image"><img src={nestImages.living} alt="Tổ ấm Nest với sofa linen màu kem, bàn trà gỗ sồi và ghế olive trong nắng sớm" width={1536} height={1024}/><span>THE WARM LIVING EDIT <span>01 / NEST</span></span></div>
    </section>

    <div className="nest-services nest-container">
      <Link to="/support/shipping"><TruckDelivery size={29} strokeWidth={1.2}/><div><strong>Giao hàng tận nhà</strong><span>Miễn phí cho đơn từ 1 triệu đồng</span></div><ArrowUpRight size={16}/></Link>
      <Link to="/support/return-policy"><Package size={29} strokeWidth={1.2}/><div><strong>An tâm khi lựa chọn</strong><span>Chính sách đổi trả rõ ràng</span></div><ArrowUpRight size={16}/></Link>
      <Link to="/contact"><Headset size={29} strokeWidth={1.2}/><div><strong>Cùng bạn chăm chút tổ ấm</strong><span>Hỗ trợ và tư vấn sản phẩm</span></div><ArrowUpRight size={16}/></Link>
    </div>

    <section className="nest-section nest-container" id="spaces">
      <SectionHeading eyebrow="KHÔNG GIAN SỐNG, NHIỀU CẢM HỨNG HƠN" title="Mỗi căn phòng, một câu chuyện." href="/all-categories" action="Khám phá danh mục"/>
      <div className="nest-spaces-grid">{rooms.map((room, i) => <Link to={room.href} className={`nest-space nest-space-${i}`} key={room.title}>
        <img src={room.image} alt={room.title + ' với nội thất tông gỗ và kem'} loading="lazy"/>
        <div><h3>{room.title}</h3><p>{room.subtitle}</p></div><span className="nest-circle"><ArrowUpRight size={20}/></span>
      </Link>)}</div>
    </section>

    <FeaturedProducts/>
    <ShopTheRoom/>

    <section className="nest-materials nest-container nest-section">
      <div><p className="nest-eyebrow">VẺ ĐẸP ĐẾN TỪ NHỮNG ĐIỀU THẬT</p><h2>Gỗ ấm.<br/>Vải mềm.<br/><em>Nhà bình yên.</em></h2><p>Một đường vân gỗ, một nếp vải mềm, một sắc màu dịu mắt. Những điều nhỏ bé làm nên cảm giác thân thuộc — và một ngôi nhà mang dấu ấn của bạn.</p><button type="button" className="nest-text-link" onClick={() => setArticle(2)}>Khám phá & chăm sóc chất liệu <ArrowUpRight size={17}/></button></div>
      <figure><img src={nestImages.materials} alt="Cận cảnh vân gỗ sồi tự nhiên cạnh vải linen mộc" loading="lazy"/><figcaption><span>01 — GỖ TỰ NHIÊN</span><span>02 — VẢI DỆT MỘC</span></figcaption></figure>
    </section>

    <section className="nest-section nest-collections" id="collections"><div className="nest-container">
      <SectionHeading eyebrow="NHỮNG BỘ SƯU TẬP CẢM HỨNG" title="Cho nhịp sống của riêng bạn."/>
      <div className="nest-collection-grid">{[
        { title: 'Sáng chậm cuối tuần', text: 'Một chỗ ngồi êm, một tách trà. Thế là đủ.', image: nestImages.living, href: '/category/sofa', tag: '01 / SỐNG CHẬM' },
        { title: 'Góc nhỏ, ý tưởng lớn', text: 'Một góc làm việc gọn gàng để cảm hứng ghé qua.', image: nestImages.workspace, href: '/category/van-phong-tai-nha', tag: '02 / LÀM VIỆC THẢNH THƠI' },
        { title: 'Bữa cơm, chuyện nhà', text: 'Giữ những khoảnh khắc bên nhau lâu hơn một chút.', image: nestImages.dining, href: '/category/do-bep', tag: '03 / CÙNG NHAU' },
      ].map((item) => <Link to={item.href} className="nest-collection" key={item.title}><div><img src={item.image} alt={item.title} loading="lazy"/><span>{item.tag}</span></div><h3>{item.title}<ArrowUpRight size={21}/></h3><p>{item.text}</p></Link>)}</div>
    </div></section>

    <section className="nest-small-space"><img src={nestImages.workspace} alt="Góc làm việc nhỏ với bàn gỗ, kệ mở và ánh sáng tự nhiên" loading="lazy"/><div><p className="nest-eyebrow">ÍT HƠN, NHƯNG VỪA ĐỦ</p><h2>Diện tích nhỏ.<br/>Cảm hứng lớn.</h2><p>Không cần thật nhiều đồ để có một ngôi nhà đẹp. Chỉ cần những món đồ phù hợp với cách bạn sống.</p><Link className="nest-button" to="/category/do-luu-tru">Khám phá giải pháp lưu trữ <ArrowRight size={17}/></Link></div></section>

    <section className="nest-section nest-container" id="journal"><SectionHeading eyebrow="NHẬT KÝ NEST" title="Nhà đẹp từ những điều giản dị."/>
      <div className="nest-journal-grid">{journals.map((item, index) => <article className="nest-journal" key={item.title}><button className="nest-journal-photo" type="button" tabIndex={-1} aria-hidden="true" onClick={() => setArticle(index)}><img src={item.image} alt="" loading="lazy"/></button><p className="nest-eyebrow">{item.tag}</p><h3><button type="button" onClick={() => setArticle(index)}>{item.title}</button></h3><p>{item.intro}</p><button type="button" className="nest-text-link" onClick={() => setArticle(index)} aria-label={`Đọc bài: ${item.title}`}>Đọc câu chuyện <ArrowRight size={16}/></button></article>)}</div>
    </section>

    <section className="nest-section nest-container nest-gallery"><SectionHeading eyebrow="LƯU LẠI MỘT CHÚT CẢM HỨNG" title="Bạn hình dung tổ ấm mình thế nào?"/><p className="nest-gallery-intro">Bốn góc nhìn, vô vàn cách để biến một không gian thành nhà. Hình ảnh phối cảnh gợi ý từ Nest.</p><div className="nest-gallery-grid">{rooms.map((room) => <Link key={room.title} to={room.href}><img src={room.image} alt={`Cảm hứng ${room.title.toLowerCase()}`} loading="lazy"/><span>{room.title}<ArrowUpRight size={16}/></span></Link>)}</div></section>

    <section className="nest-consultation"><Leaf size={30} strokeWidth={1}/><p className="nest-eyebrow">MỘT TỔ ẤM BẮT ĐẦU TỪ MỘT CUỘC TRÒ CHUYỆN</p><h2>Cùng bạn tìm điều phù hợp.</h2><p>Cần chọn một chiếc ghế, phối một góc phòng hay tìm hiểu chất liệu?<br/>Nest luôn sẵn lòng lắng nghe.</p><Link className="nest-button" to="/contact">Trò chuyện cùng Nest <ArrowUpRight size={18}/></Link></section>

    <Modal opened={selectedArticle !== null} onClose={() => setArticle(null)} title="NHẬT KÝ NEST" size="lg" centered closeButtonLabel="Đóng bài viết" styles={{ modal: { backgroundColor: '#f6f2eb', borderRadius: 2 }, title: { letterSpacing: 2, fontSize: 12 } }}>
      {selectedArticle && <article className="nest-article"><img src={selectedArticle.image} alt=""/><h2>{selectedArticle.title}</h2>{selectedArticle.paragraphs.map((paragraph) => <p key={paragraph}>{paragraph}</p>)}<Link className="nest-button" to={selectedArticle.href}>{selectedArticle.cta} <ArrowRight size={17}/></Link></article>}
    </Modal>
  </main>;
}
export default ClientHome;
