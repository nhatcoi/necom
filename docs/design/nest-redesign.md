# Nest storefront redesign

## Approved direction
Warm Living: ivory, olive, oak and linen; editorial serif headlines, large room photography and a practical shopping experience. Based on the three approved mockups in this conversation.

## Implementation plan
1. Scope the visual system to the customer storefront; preserve admin styles and existing cart/backend edits.
2. Replace storefront navigation/footer, retaining search, dynamic categories, account, notifications, wishlist and cart access. Support keyboard and mobile navigation.
3. Rebuild the homepage: split hero, services, room discovery, live product selection, interactive room inspiration, materials, collections, small-space story, editorial guides, inspiration gallery and consultation CTA.
4. Generate five owned-by-project illustrative interior assets, optimize to local WebP and retain provenance/prompts. Use catalog thumbnails and prices from the API for actual merchandise; label scene imagery as inspiration.
5. Keep every CTA functional: existing routes, section anchors, and accessible guide dialogs. Newsletter has no backend endpoint; use the existing contact/tư vấn flow instead of claiming a subscription was saved. Do not fabricate customer testimonials, popularity counts, or swatches.
6. Validate TypeScript, production build, desktop/mobile layout, search/category/product navigation, keyboard controls and API empty/error behavior.

## Boundaries
This change implements the homepage and shared customer shell. Existing category, search, product-detail and checkout business logic is retained. No database migrations, production deployment or order mutations are needed.

## Verification
- TypeScript and production build pass. The build retains pre-existing lint warnings in LoadingMiddleware, product-variant components, address selection and promotion view models, plus the existing bundle-size warning.
- 8 focused React tests pass: discounted minimum price, explicit multi-variant selection, authenticated cart payload, signed-out cart guard, independent wishlist action, product API retry, empty catalog, and exclusive room hotspots.
- Browser verified against local backend: catalog/category data, Vietnamese search including ampersand encoding, product-detail navigation, product-group switching, article dialog, mobile menu and Escape dismissal.
- Responsive overflow checks: 320px, 390px, 768px and normal desktop viewport.
- Development preview: http://localhost:3000. Production has not been deployed.

## Assets and screenshots
Five local WebP scene images total approximately 1.1 MB in `necom-client/public/images/nest/`. Their complete built-in image_gen prompts are in that directory's README.md. Catalog photos/prices remain live API data. Desktop and mobile screenshots are in this design directory.
