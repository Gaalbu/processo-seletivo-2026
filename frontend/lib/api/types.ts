export type UserRole = "ADMIN" | "CUSTOMER";

export type AuthUser = {
  id: string;
  name: string;
  email: string;
  role: UserRole;
};

export type AuthResponse = {
  token: string;
  user: AuthUser;
};

export type Product = {
  id: string;
  name: string;
  description: string;
  price: number;
  stock: number;
  category: string;
  imageUrl: string;
};

export type ProductPage = {
  content: Product[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
};

export type CartItem = {
  id: string;
  productId: string;
  productName: string;
  unitPrice: number;
  quantity: number;
  availableStock: number;
  imageUrl: string;
  lineTotal: number;
};

export type Cart = {
  id: string;
  items: CartItem[];
  subtotal: number;
};

export type OrderStatus = "PENDING" | "PAID" | "SHIPPED" | "DELIVERED" | "CANCELLED";
export type PaymentStatus = "PENDING" | "APPROVED" | "FAILED";

export type OrderItem = {
  id: string;
  productId: string;
  productName: string;
  unitPrice: number;
  quantity: number;
  lineTotal: number;
};

export type Order = {
  id: string;
  status: OrderStatus;
  paymentStatus: PaymentStatus;
  couponCode: string | null;
  subtotalAmount: number;
  discountAmount: number;
  totalAmount: number;
  paymentUrl: string | null;
  createdAt: string;
  items: OrderItem[];
};
