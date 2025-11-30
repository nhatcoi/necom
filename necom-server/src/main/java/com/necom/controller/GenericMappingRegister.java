package com.necom.controller;

import com.necom.constant.ResourceName;
import com.necom.constant.SearchFields;
import com.necom.dto.address.AddressRequest;
import com.necom.dto.address.AddressResponse;
import com.necom.dto.address.DistrictRequest;
import com.necom.dto.address.DistrictResponse;
import com.necom.dto.address.ProvinceRequest;
import com.necom.dto.address.ProvinceResponse;
import com.necom.dto.address.WardRequest;
import com.necom.dto.address.WardResponse;
import com.necom.dto.authentication.RoleRequest;
import com.necom.dto.authentication.RoleResponse;
import com.necom.dto.authentication.UserRequest;
import com.necom.dto.authentication.UserResponse;
import com.necom.dto.cashbook.PaymentMethodRequest;
import com.necom.dto.cashbook.PaymentMethodResponse;
import com.necom.dto.chat.RoomRequest;
import com.necom.dto.chat.RoomResponse;
import com.necom.dto.customer.CustomerGroupRequest;
import com.necom.dto.customer.CustomerGroupResponse;
import com.necom.dto.customer.CustomerRequest;
import com.necom.dto.customer.CustomerResourceRequest;
import com.necom.dto.customer.CustomerResourceResponse;
import com.necom.dto.customer.CustomerResponse;
import com.necom.dto.customer.CustomerStatusRequest;
import com.necom.dto.customer.CustomerStatusResponse;
import com.necom.dto.employee.DepartmentRequest;
import com.necom.dto.employee.DepartmentResponse;
import com.necom.dto.employee.EmployeeRequest;
import com.necom.dto.employee.EmployeeResponse;
import com.necom.dto.employee.JobLevelRequest;
import com.necom.dto.employee.JobLevelResponse;
import com.necom.dto.employee.JobTitleRequest;
import com.necom.dto.employee.JobTitleResponse;
import com.necom.dto.employee.JobTypeRequest;
import com.necom.dto.employee.JobTypeResponse;
import com.necom.dto.employee.OfficeRequest;
import com.necom.dto.employee.OfficeResponse;
import com.necom.dto.general.ImageRequest;
import com.necom.dto.general.ImageResponse;
import com.necom.dto.inventory.CountRequest;
import com.necom.dto.inventory.CountResponse;
import com.necom.dto.inventory.DestinationRequest;
import com.necom.dto.inventory.DestinationResponse;
import com.necom.dto.inventory.DocketReasonRequest;
import com.necom.dto.inventory.DocketReasonResponse;
import com.necom.dto.inventory.DocketRequest;
import com.necom.dto.inventory.DocketResponse;
import com.necom.dto.inventory.ProductInventoryLimitRequest;
import com.necom.dto.inventory.ProductInventoryLimitResponse;
import com.necom.dto.inventory.PurchaseOrderRequest;
import com.necom.dto.inventory.PurchaseOrderResponse;
import com.necom.dto.inventory.StorageLocationRequest;
import com.necom.dto.inventory.StorageLocationResponse;
import com.necom.dto.inventory.TransferRequest;
import com.necom.dto.inventory.TransferResponse;
import com.necom.dto.inventory.VariantInventoryLimitRequest;
import com.necom.dto.inventory.VariantInventoryLimitResponse;
import com.necom.dto.inventory.WarehouseRequest;
import com.necom.dto.inventory.WarehouseResponse;
import com.necom.dto.order.OrderCancellationReasonRequest;
import com.necom.dto.order.OrderCancellationReasonResponse;
import com.necom.dto.order.OrderRequest;
import com.necom.dto.order.OrderResourceRequest;
import com.necom.dto.order.OrderResourceResponse;
import com.necom.dto.order.OrderResponse;
import com.necom.dto.product.BrandRequest;
import com.necom.dto.product.BrandResponse;
import com.necom.dto.product.CategoryRequest;
import com.necom.dto.product.CategoryResponse;
import com.necom.dto.product.GuaranteeRequest;
import com.necom.dto.product.GuaranteeResponse;
import com.necom.dto.product.ProductRequest;
import com.necom.dto.product.ProductResponse;
import com.necom.dto.product.PropertyRequest;
import com.necom.dto.product.PropertyResponse;
import com.necom.dto.product.SpecificationRequest;
import com.necom.dto.product.SpecificationResponse;
import com.necom.dto.product.SupplierRequest;
import com.necom.dto.product.SupplierResponse;
import com.necom.dto.product.TagRequest;
import com.necom.dto.product.TagResponse;
import com.necom.dto.product.UnitRequest;
import com.necom.dto.product.UnitResponse;
import com.necom.dto.product.VariantRequest;
import com.necom.dto.product.VariantResponse;
import com.necom.dto.promotion.PromotionRequest;
import com.necom.dto.promotion.PromotionResponse;
import com.necom.dto.review.ReviewRequest;
import com.necom.dto.review.ReviewResponse;
import com.necom.dto.reward.RewardStrategyRequest;
import com.necom.dto.reward.RewardStrategyResponse;
import com.necom.dto.waybill.WaybillRequest;
import com.necom.dto.waybill.WaybillResponse;
import com.necom.entity.address.Address;
import com.necom.entity.address.District;
import com.necom.entity.address.Ward;
import com.necom.entity.authentication.Role;
import com.necom.entity.authentication.User;
import com.necom.entity.cashbook.PaymentMethod;
import com.necom.entity.chat.Room;
import com.necom.entity.customer.Customer;
import com.necom.entity.customer.CustomerGroup;
import com.necom.entity.customer.CustomerResource;
import com.necom.entity.customer.CustomerStatus;
import com.necom.entity.employee.Department;
import com.necom.entity.employee.Employee;
import com.necom.entity.employee.JobLevel;
import com.necom.entity.employee.JobTitle;
import com.necom.entity.employee.JobType;
import com.necom.entity.employee.Office;
import com.necom.entity.general.Image;
import com.necom.entity.inventory.Count;
import com.necom.entity.inventory.Destination;
import com.necom.entity.inventory.DocketReason;
import com.necom.entity.inventory.ProductInventoryLimit;
import com.necom.entity.inventory.PurchaseOrder;
import com.necom.entity.inventory.StorageLocation;
import com.necom.entity.inventory.Transfer;
import com.necom.entity.inventory.VariantInventoryLimit;
import com.necom.entity.inventory.Warehouse;
import com.necom.entity.order.Order;
import com.necom.entity.order.OrderCancellationReason;
import com.necom.entity.order.OrderResource;
import com.necom.entity.product.Brand;
import com.necom.entity.product.Category;
import com.necom.entity.product.Guarantee;
import com.necom.entity.product.Product;
import com.necom.entity.product.Property;
import com.necom.entity.product.Specification;
import com.necom.entity.product.Supplier;
import com.necom.entity.product.Tag;
import com.necom.entity.product.Unit;
import com.necom.entity.product.Variant;
import com.necom.entity.reward.RewardStrategy;
import com.necom.mapper.address.AddressMapper;
import com.necom.mapper.address.DistrictMapper;
import com.necom.mapper.address.WardMapper;
import com.necom.mapper.authentication.RoleMapper;
import com.necom.mapper.authentication.UserMapper;
import com.necom.mapper.cashbook.PaymentMethodMapper;
import com.necom.mapper.chat.RoomMapper;
import com.necom.mapper.customer.CustomerGroupMapper;
import com.necom.mapper.customer.CustomerMapper;
import com.necom.mapper.customer.CustomerResourceMapper;
import com.necom.mapper.customer.CustomerStatusMapper;
import com.necom.mapper.employee.DepartmentMapper;
import com.necom.mapper.employee.EmployeeMapper;
import com.necom.mapper.employee.JobLevelMapper;
import com.necom.mapper.employee.JobTitleMapper;
import com.necom.mapper.employee.JobTypeMapper;
import com.necom.mapper.employee.OfficeMapper;
import com.necom.mapper.general.ImageMapper;
import com.necom.mapper.inventory.CountMapper;
import com.necom.mapper.inventory.DestinationMapper;
import com.necom.mapper.inventory.DocketReasonMapper;
import com.necom.mapper.inventory.ProductInventoryLimitMapper;
import com.necom.mapper.inventory.PurchaseOrderMapper;
import com.necom.mapper.inventory.StorageLocationMapper;
import com.necom.mapper.inventory.TransferMapper;
import com.necom.mapper.inventory.VariantInventoryLimitMapper;
import com.necom.mapper.inventory.WarehouseMapper;
import com.necom.mapper.order.OrderCancellationReasonMapper;
import com.necom.mapper.order.OrderMapper;
import com.necom.mapper.order.OrderResourceMapper;
import com.necom.mapper.product.BrandMapper;
import com.necom.mapper.product.CategoryMapper;
import com.necom.mapper.product.GuaranteeMapper;
import com.necom.mapper.product.ProductMapper;
import com.necom.mapper.product.PropertyMapper;
import com.necom.mapper.product.SpecificationMapper;
import com.necom.mapper.product.SupplierMapper;
import com.necom.mapper.product.TagMapper;
import com.necom.mapper.product.UnitMapper;
import com.necom.mapper.product.VariantMapper;
import com.necom.mapper.reward.RewardStrategyMapper;
import com.necom.repository.address.AddressRepository;
import com.necom.repository.address.DistrictRepository;
import com.necom.repository.address.WardRepository;
import com.necom.repository.authentication.RoleRepository;
import com.necom.repository.authentication.UserRepository;
import com.necom.repository.cashbook.PaymentMethodRepository;
import com.necom.repository.chat.RoomRepository;
import com.necom.repository.customer.CustomerGroupRepository;
import com.necom.repository.customer.CustomerRepository;
import com.necom.repository.customer.CustomerResourceRepository;
import com.necom.repository.customer.CustomerStatusRepository;
import com.necom.repository.employee.DepartmentRepository;
import com.necom.repository.employee.EmployeeRepository;
import com.necom.repository.employee.JobLevelRepository;
import com.necom.repository.employee.JobTitleRepository;
import com.necom.repository.employee.JobTypeRepository;
import com.necom.repository.employee.OfficeRepository;
import com.necom.repository.general.ImageRepository;
import com.necom.repository.inventory.CountRepository;
import com.necom.repository.inventory.DestinationRepository;
import com.necom.repository.inventory.DocketReasonRepository;
import com.necom.repository.inventory.ProductInventoryLimitRepository;
import com.necom.repository.inventory.PurchaseOrderRepository;
import com.necom.repository.inventory.StorageLocationRepository;
import com.necom.repository.inventory.TransferRepository;
import com.necom.repository.inventory.VariantInventoryLimitRepository;
import com.necom.repository.inventory.WarehouseRepository;
import com.necom.repository.order.OrderCancellationReasonRepository;
import com.necom.repository.order.OrderRepository;
import com.necom.repository.order.OrderResourceRepository;
import com.necom.repository.product.BrandRepository;
import com.necom.repository.product.CategoryRepository;
import com.necom.repository.product.GuaranteeRepository;
import com.necom.repository.product.ProductRepository;
import com.necom.repository.product.PropertyRepository;
import com.necom.repository.product.SpecificationRepository;
import com.necom.repository.product.SupplierRepository;
import com.necom.repository.product.TagRepository;
import com.necom.repository.product.UnitRepository;
import com.necom.repository.product.VariantRepository;
import com.necom.repository.reward.RewardStrategyRepository;
import com.necom.service.CrudService;
import com.necom.service.GenericService;
import com.necom.service.address.ProvinceService;
import com.necom.service.inventory.DocketService;
import com.necom.service.promotion.PromotionService;
import com.necom.service.review.ReviewService;
import com.necom.service.waybill.WaybillService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPatternParser;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
@AllArgsConstructor
public class GenericMappingRegister {

    private ApplicationContext context;
    private RequestMappingHandlerMapping handlerMapping;

    // Controllers
    private GenericController<ProvinceRequest, ProvinceResponse> provinceController;
    private GenericController<DistrictRequest, DistrictResponse> districtController;
    private GenericController<WardRequest, WardResponse> wardController;
    private GenericController<AddressRequest, AddressResponse> addressController;
    private GenericController<UserRequest, UserResponse> userController;
    private GenericController<RoleRequest, RoleResponse> roleController;
    private GenericController<OfficeRequest, OfficeResponse> officeController;
    private GenericController<DepartmentRequest, DepartmentResponse> departmentController;
    private GenericController<JobLevelRequest, JobLevelResponse> jobLevelController;
    private GenericController<JobTypeRequest, JobTypeResponse> jobTypeController;
    private GenericController<JobTitleRequest, JobTitleResponse> jobTitleController;
    private GenericController<EmployeeRequest, EmployeeResponse> employeeController;
    private GenericController<CustomerGroupRequest, CustomerGroupResponse> customerGroupController;
    private GenericController<CustomerResourceRequest, CustomerResourceResponse> customerResourceController;
    private GenericController<CustomerStatusRequest, CustomerStatusResponse> customerStatusController;
    private GenericController<CustomerRequest, CustomerResponse> customerController;
    private GenericController<PropertyRequest, PropertyResponse> propertyController;
    private GenericController<CategoryRequest, CategoryResponse> categoryController;
    private GenericController<TagRequest, TagResponse> tagController;
    private GenericController<GuaranteeRequest, GuaranteeResponse> guaranteeController;
    private GenericController<UnitRequest, UnitResponse> unitController;
    private GenericController<SupplierRequest, SupplierResponse> supplierController;
    private GenericController<BrandRequest, BrandResponse> brandController;
    private GenericController<SpecificationRequest, SpecificationResponse> specificationController;
    private GenericController<ProductRequest, ProductResponse> productController;
    private GenericController<VariantRequest, VariantResponse> variantController;
    private GenericController<ImageRequest, ImageResponse> imageController;
    private GenericController<ProductInventoryLimitRequest, ProductInventoryLimitResponse> productInventoryLimitController;
    private GenericController<VariantInventoryLimitRequest, VariantInventoryLimitResponse> variantInventoryLimitController;
    private GenericController<WarehouseRequest, WarehouseResponse> warehouseController;
    private GenericController<CountRequest, CountResponse> countController;
    private GenericController<DestinationRequest, DestinationResponse> destinationController;
    private GenericController<DocketReasonRequest, DocketReasonResponse> docketReasonController;
    private GenericController<TransferRequest, TransferResponse> transferController;
    private GenericController<DocketRequest, DocketResponse> docketController;
    private GenericController<StorageLocationRequest, StorageLocationResponse> storageLocationController;
    private GenericController<PurchaseOrderRequest, PurchaseOrderResponse> purchaseOrderController;
    private GenericController<OrderResourceRequest, OrderResourceResponse> orderResourceController;
    private GenericController<OrderCancellationReasonRequest, OrderCancellationReasonResponse> orderCancellationReasonController;
    private GenericController<OrderRequest, OrderResponse> orderController;
    private GenericController<WaybillRequest, WaybillResponse> waybillController;
    private GenericController<ReviewRequest, ReviewResponse> reviewController;
    private GenericController<PaymentMethodRequest, PaymentMethodResponse> paymentMethodController;
    private GenericController<PromotionRequest, PromotionResponse> promotionController;
    private GenericController<RoomRequest, RoomResponse> roomController;
    private GenericController<RewardStrategyRequest, RewardStrategyResponse> rewardStrategyController;

    // Services
    private GenericService<District, DistrictRequest, DistrictResponse> districtService;
    private GenericService<Ward, WardRequest, WardResponse> wardService;
    private GenericService<Address, AddressRequest, AddressResponse> addressService;
    private GenericService<User, UserRequest, UserResponse> userService;
    private GenericService<Role, RoleRequest, RoleResponse> roleService;
    private GenericService<Office, OfficeRequest, OfficeResponse> officeService;
    private GenericService<Department, DepartmentRequest, DepartmentResponse> departmentService;
    private GenericService<JobLevel, JobLevelRequest, JobLevelResponse> jobLevelService;
    private GenericService<JobType, JobTypeRequest, JobTypeResponse> jobTypeService;
    private GenericService<JobTitle, JobTitleRequest, JobTitleResponse> jobTitleService;
    private GenericService<Employee, EmployeeRequest, EmployeeResponse> employeeService;
    private GenericService<CustomerGroup, CustomerGroupRequest, CustomerGroupResponse> customerGroupService;
    private GenericService<CustomerResource, CustomerResourceRequest, CustomerResourceResponse> customerResourceService;
    private GenericService<CustomerStatus, CustomerStatusRequest, CustomerStatusResponse> customerStatusService;
    private GenericService<Customer, CustomerRequest, CustomerResponse> customerService;
    private GenericService<Property, PropertyRequest, PropertyResponse> propertyService;
    private GenericService<Category, CategoryRequest, CategoryResponse> categoryService;
    private GenericService<Tag, TagRequest, TagResponse> tagService;
    private GenericService<Guarantee, GuaranteeRequest, GuaranteeResponse> guaranteeService;
    private GenericService<Unit, UnitRequest, UnitResponse> unitService;
    private GenericService<Supplier, SupplierRequest, SupplierResponse> supplierService;
    private GenericService<Brand, BrandRequest, BrandResponse> brandService;
    private GenericService<Specification, SpecificationRequest, SpecificationResponse> specificationService;
    private GenericService<Product, ProductRequest, ProductResponse> productService;
    private GenericService<Variant, VariantRequest, VariantResponse> variantService;
    private GenericService<Image, ImageRequest, ImageResponse> imageService;
    private GenericService<ProductInventoryLimit, ProductInventoryLimitRequest, ProductInventoryLimitResponse> productInventoryLimitService;
    private GenericService<VariantInventoryLimit, VariantInventoryLimitRequest, VariantInventoryLimitResponse> variantInventoryLimitService;
    private GenericService<Warehouse, WarehouseRequest, WarehouseResponse> warehouseService;
    private GenericService<Count, CountRequest, CountResponse> countService;
    private GenericService<Destination, DestinationRequest, DestinationResponse> destinationService;
    private GenericService<DocketReason, DocketReasonRequest, DocketReasonResponse> docketReasonService;
    private GenericService<Transfer, TransferRequest, TransferResponse> transferService;
    private GenericService<StorageLocation, StorageLocationRequest, StorageLocationResponse> storageLocationService;
    private GenericService<PurchaseOrder, PurchaseOrderRequest, PurchaseOrderResponse> purchaseOrderService;
    private GenericService<OrderResource, OrderResourceRequest, OrderResourceResponse> orderResourceService;
    private GenericService<OrderCancellationReason, OrderCancellationReasonRequest, OrderCancellationReasonResponse> orderCancellationReasonService;
    private GenericService<Order, OrderRequest, OrderResponse> orderService;
    private GenericService<PaymentMethod, PaymentMethodRequest, PaymentMethodResponse> paymentMethodService;
    private GenericService<Room, RoomRequest, RoomResponse> roomService;
    private GenericService<RewardStrategy, RewardStrategyRequest, RewardStrategyResponse> rewardStrategyService;

    @PostConstruct
    public void registerControllers() throws NoSuchMethodException {

        register("provinces", provinceController, context.getBean(ProvinceService.class), ProvinceRequest.class);

        register("districts", districtController, districtService.init(
                context.getBean(DistrictRepository.class),
                context.getBean(DistrictMapper.class),
                SearchFields.DISTRICT,
                ResourceName.DISTRICT
        ), DistrictRequest.class);

        register("wards", wardController, wardService.init(
                context.getBean(WardRepository.class),
                context.getBean(WardMapper.class),
                SearchFields.WARD,
                ResourceName.WARD
        ), WardRequest.class);

        register("addresses", addressController, addressService.init(
                context.getBean(AddressRepository.class),
                context.getBean(AddressMapper.class),
                SearchFields.ADDRESS,
                ResourceName.ADDRESS
        ), AddressRequest.class);

        register("users", userController, userService.init(
                context.getBean(UserRepository.class),
                context.getBean(UserMapper.class),
                SearchFields.USER,
                ResourceName.USER
        ), UserRequest.class);

        register("roles", roleController, roleService.init(
                context.getBean(RoleRepository.class),
                context.getBean(RoleMapper.class),
                SearchFields.ROLE,
                ResourceName.ROLE
        ), RoleRequest.class);

        register("offices", officeController, officeService.init(
                context.getBean(OfficeRepository.class),
                context.getBean(OfficeMapper.class),
                SearchFields.OFFICE,
                ResourceName.OFFICE
        ), OfficeRequest.class);

        register("departments", departmentController, departmentService.init(
                context.getBean(DepartmentRepository.class),
                context.getBean(DepartmentMapper.class),
                SearchFields.DEPARTMENT,
                ResourceName.DEPARTMENT
        ), DepartmentRequest.class);

        register("job-levels", jobLevelController, jobLevelService.init(
                context.getBean(JobLevelRepository.class),
                context.getBean(JobLevelMapper.class),
                SearchFields.JOB_LEVEL,
                ResourceName.JOB_LEVEL
        ), JobLevelRequest.class);

        register("job-titles", jobTitleController, jobTitleService.init(
                context.getBean(JobTitleRepository.class),
                context.getBean(JobTitleMapper.class),
                SearchFields.JOB_TITLE,
                ResourceName.JOB_TITLE
        ), JobTitleRequest.class);

        register("job-types", jobTypeController, jobTypeService.init(
                context.getBean(JobTypeRepository.class),
                context.getBean(JobTypeMapper.class),
                SearchFields.JOB_TYPE,
                ResourceName.JOB_TYPE
        ), JobTypeRequest.class);

        register("employees", employeeController, employeeService.init(
                context.getBean(EmployeeRepository.class),
                context.getBean(EmployeeMapper.class),
                SearchFields.EMPLOYEE,
                ResourceName.EMPLOYEE
        ), EmployeeRequest.class);

        register("customer-groups", customerGroupController, customerGroupService.init(
                context.getBean(CustomerGroupRepository.class),
                context.getBean(CustomerGroupMapper.class),
                SearchFields.CUSTOMER_GROUP,
                ResourceName.CUSTOMER_GROUP
        ), CustomerGroupRequest.class);

        register("customer-resources", customerResourceController, customerResourceService.init(
                context.getBean(CustomerResourceRepository.class),
                context.getBean(CustomerResourceMapper.class),
                SearchFields.CUSTOMER_RESOURCE,
                ResourceName.CUSTOMER_RESOURCE
        ), CustomerResourceRequest.class);

        register("customer-status", customerStatusController, customerStatusService.init(
                context.getBean(CustomerStatusRepository.class),
                context.getBean(CustomerStatusMapper.class),
                SearchFields.CUSTOMER_STATUS,
                ResourceName.CUSTOMER_STATUS
        ), CustomerStatusRequest.class);

        register("customers", customerController, customerService.init(
                context.getBean(CustomerRepository.class),
                context.getBean(CustomerMapper.class),
                SearchFields.CUSTOMER,
                ResourceName.CUSTOMER
        ), CustomerRequest.class);

        register("properties", propertyController, propertyService.init(
                context.getBean(PropertyRepository.class),
                context.getBean(PropertyMapper.class),
                SearchFields.PROPERTY,
                ResourceName.PROPERTY
        ), PropertyRequest.class);

        register("categories", categoryController, categoryService.init(
                context.getBean(CategoryRepository.class),
                context.getBean(CategoryMapper.class),
                SearchFields.CATEGORY,
                ResourceName.CATEGORY
        ), CategoryRequest.class);

        register("tags", tagController, tagService.init(
                context.getBean(TagRepository.class),
                context.getBean(TagMapper.class),
                SearchFields.TAG,
                ResourceName.TAG
        ), TagRequest.class);

        register("guarantees", guaranteeController, guaranteeService.init(
                context.getBean(GuaranteeRepository.class),
                context.getBean(GuaranteeMapper.class),
                SearchFields.GUARANTEE,
                ResourceName.GUARANTEE
        ), GuaranteeRequest.class);

        register("units", unitController, unitService.init(
                context.getBean(UnitRepository.class),
                context.getBean(UnitMapper.class),
                SearchFields.UNIT,
                ResourceName.UNIT
        ), UnitRequest.class);

        register("suppliers", supplierController, supplierService.init(
                context.getBean(SupplierRepository.class),
                context.getBean(SupplierMapper.class),
                SearchFields.SUPPLIER,
                ResourceName.SUPPLIER
        ), SupplierRequest.class);

        register("brands", brandController, brandService.init(
                context.getBean(BrandRepository.class),
                context.getBean(BrandMapper.class),
                SearchFields.BRAND,
                ResourceName.BRAND
        ), BrandRequest.class);

        register("specifications", specificationController, specificationService.init(
                context.getBean(SpecificationRepository.class),
                context.getBean(SpecificationMapper.class),
                SearchFields.SPECIFICATION,
                ResourceName.SPECIFICATION
        ), SpecificationRequest.class);

        register("products", productController, productService.init(
                context.getBean(ProductRepository.class),
                context.getBean(ProductMapper.class),
                SearchFields.PRODUCT,
                ResourceName.PRODUCT
        ), ProductRequest.class);

        register("variants", variantController, variantService.init(
                context.getBean(VariantRepository.class),
                context.getBean(VariantMapper.class),
                SearchFields.VARIANT,
                ResourceName.VARIANT
        ), VariantRequest.class);

        register("images", imageController, imageService.init(
                context.getBean(ImageRepository.class),
                context.getBean(ImageMapper.class),
                SearchFields.IMAGE,
                ResourceName.IMAGE
        ), ImageRequest.class);

        register("product-inventory-limits", productInventoryLimitController, productInventoryLimitService.init(
                context.getBean(ProductInventoryLimitRepository.class),
                context.getBean(ProductInventoryLimitMapper.class),
                SearchFields.PRODUCT_INVENTORY_LIMIT,
                ResourceName.PRODUCT_INVENTORY_LIMIT
        ), ProductInventoryLimitRequest.class);

        register("variant-inventory-limits", variantInventoryLimitController, variantInventoryLimitService.init(
                context.getBean(VariantInventoryLimitRepository.class),
                context.getBean(VariantInventoryLimitMapper.class),
                SearchFields.VARIANT_INVENTORY_LIMIT,
                ResourceName.VARIANT_INVENTORY_LIMIT
        ), VariantInventoryLimitRequest.class);

        register("warehouses", warehouseController, warehouseService.init(
                context.getBean(WarehouseRepository.class),
                context.getBean(WarehouseMapper.class),
                SearchFields.WAREHOUSE,
                ResourceName.WAREHOUSE
        ), WarehouseRequest.class);

        register("counts", countController, countService.init(
                context.getBean(CountRepository.class),
                context.getBean(CountMapper.class),
                SearchFields.COUNT,
                ResourceName.COUNT
        ), CountRequest.class);

        register("destinations", destinationController, destinationService.init(
                context.getBean(DestinationRepository.class),
                context.getBean(DestinationMapper.class),
                SearchFields.DESTINATION,
                ResourceName.DESTINATION
        ), DestinationRequest.class);

        register("docket-reasons", docketReasonController, docketReasonService.init(
                context.getBean(DocketReasonRepository.class),
                context.getBean(DocketReasonMapper.class),
                SearchFields.DOCKET_REASON,
                ResourceName.DOCKET_REASON
        ), DocketReasonRequest.class);

        register("transfers", transferController, transferService.init(
                context.getBean(TransferRepository.class),
                context.getBean(TransferMapper.class),
                SearchFields.TRANSFER,
                ResourceName.TRANSFER
        ), TransferRequest.class);

        register("dockets", docketController, context.getBean(DocketService.class), DocketRequest.class);

        register("storage-locations", storageLocationController, storageLocationService.init(
                context.getBean(StorageLocationRepository.class),
                context.getBean(StorageLocationMapper.class),
                SearchFields.STORAGE_LOCATION,
                ResourceName.STORAGE_LOCATION
        ), StorageLocationRequest.class);

        register("purchase-orders", purchaseOrderController, purchaseOrderService.init(
                context.getBean(PurchaseOrderRepository.class),
                context.getBean(PurchaseOrderMapper.class),
                SearchFields.PURCHASE_ORDER,
                ResourceName.PURCHASE_ORDER
        ), PurchaseOrderRequest.class);

        register("order-resources", orderResourceController, orderResourceService.init(
                context.getBean(OrderResourceRepository.class),
                context.getBean(OrderResourceMapper.class),
                SearchFields.ORDER_RESOURCE,
                ResourceName.ORDER_RESOURCE
        ), OrderResourceRequest.class);

        register("order-cancellation-reasons", orderCancellationReasonController, orderCancellationReasonService.init(
                context.getBean(OrderCancellationReasonRepository.class),
                context.getBean(OrderCancellationReasonMapper.class),
                SearchFields.ORDER_CANCELLATION_REASON,
                ResourceName.ORDER_CANCELLATION_REASON
        ), OrderCancellationReasonRequest.class);

        register("orders", orderController, orderService.init(
                context.getBean(OrderRepository.class),
                context.getBean(OrderMapper.class),
                SearchFields.ORDER,
                ResourceName.ORDER
        ), OrderRequest.class);

        register("waybills", waybillController, context.getBean(WaybillService.class), WaybillRequest.class);

        register("reviews", reviewController, context.getBean(ReviewService.class), ReviewRequest.class);

        register("payment-methods", paymentMethodController, paymentMethodService.init(
                context.getBean(PaymentMethodRepository.class),
                context.getBean(PaymentMethodMapper.class),
                SearchFields.PAYMENT_METHOD,
                ResourceName.PAYMENT_METHOD
        ), PaymentMethodRequest.class);

        register("promotions", promotionController, context.getBean(PromotionService.class), PromotionRequest.class);

        register("rooms", roomController, roomService.init(
                context.getBean(RoomRepository.class),
                context.getBean(RoomMapper.class),
                SearchFields.ROOM,
                ResourceName.ROOM
        ), RoomRequest.class);

        register("reward-strategies", rewardStrategyController, rewardStrategyService.init(
                context.getBean(RewardStrategyRepository.class),
                context.getBean(RewardStrategyMapper.class),
                SearchFields.REWARD_STRATEGY,
                ResourceName.REWARD_STRATEGY
        ), RewardStrategyRequest.class);

    }

    private <I, O> void register(String resource,
                                 GenericController<I, O> controller,
                                 CrudService<Long, I, O> service,
                                 Class<I> requestType
    ) throws NoSuchMethodException {
        RequestMappingInfo.BuilderConfiguration options = new RequestMappingInfo.BuilderConfiguration();
        options.setPatternParser(new PathPatternParser());

        controller.setCrudService(service);
        controller.setRequestType(requestType);

        handlerMapping.registerMapping(
                RequestMappingInfo.paths("/api/" + resource)
                        .methods(RequestMethod.GET)
                        .produces(MediaType.APPLICATION_JSON_VALUE)
                        .options(options)
                        .build(),
                controller,
                controller.getClass().getMethod("getAllResources", int.class, int.class,
                        String.class, String.class, String.class, boolean.class)
        );

        handlerMapping.registerMapping(
                RequestMappingInfo.paths("/api/" + resource + "/{id}")
                        .methods(RequestMethod.GET)
                        .produces(MediaType.APPLICATION_JSON_VALUE)
                        .options(options)
                        .build(),
                controller,
                controller.getClass().getMethod("getResource", Long.class)
        );

        handlerMapping.registerMapping(
                RequestMappingInfo.paths("/api/" + resource)
                        .methods(RequestMethod.POST)
                        .consumes(MediaType.APPLICATION_JSON_VALUE)
                        .produces(MediaType.APPLICATION_JSON_VALUE)
                        .options(options)
                        .build(),
                controller,
                controller.getClass().getMethod("createResource", JsonNode.class)
        );

        handlerMapping.registerMapping(
                RequestMappingInfo.paths("/api/" + resource + "/{id}")
                        .methods(RequestMethod.PUT)
                        .consumes(MediaType.APPLICATION_JSON_VALUE)
                        .produces(MediaType.APPLICATION_JSON_VALUE)
                        .options(options)
                        .build(),
                controller,
                controller.getClass().getMethod("updateResource", Long.class, JsonNode.class)
        );

        handlerMapping.registerMapping(
                RequestMappingInfo.paths("/api/" + resource + "/{id}")
                        .methods(RequestMethod.DELETE)
                        .options(options)
                        .build(),
                controller,
                controller.getClass().getMethod("deleteResource", Long.class)
        );

        handlerMapping.registerMapping(
                RequestMappingInfo.paths("/api/" + resource)
                        .methods(RequestMethod.DELETE)
                        .consumes(MediaType.APPLICATION_JSON_VALUE)
                        .options(options)
                        .build(),
                controller,
                controller.getClass().getMethod("deleteResources", List.class)
        );
    }

}
