Đặt ảnh PNG cho các vật thể trên map vào các thư mục sau:

1. Cây xoài (MangoTree):
   assets/objects/mango_tree/
   - Ảnh tĩnh: mango_tree.png
   - Hoặc animation 4 frame: mango_tree_0.png .. mango_tree_3.png

2. Cửa hàng / Thương nhân (Merchant / Shop):
   assets/objects/shop/
   - Ảnh tĩnh: shop.png
   - Hoặc animation 4 frame: shop_0.png .. shop_3.png

Ghi chú:
- Nếu chưa có ảnh, game sẽ tự động vẽ khối màu placeholder như cũ (không bị lỗi).
- Để thêm vật thể mới trên map, chỉ cần tạo class dùng MapObjectSprite("objects/<ten>/<ten>").
