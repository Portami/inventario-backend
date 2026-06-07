-- DEV demo data: product catalogue, variants and product inventory.

INSERT INTO category (id, name) VALUES
    (1, 'Taschen'),
    (2, 'Kinderfinken');

INSERT INTO product (id, name, category_id) VALUES
    (1, 'shopper-1', 1),
    (2, 'shopper-2', 1),
    (3, 'täppali', 2);

INSERT INTO product_variant (id, name, price, product_id) VALUES
    (1, 'weiss/schwarz', 169.00, 1),
    (2, 'giftgrün/grau', 169.00, 1),
    (3, 'petrol/anthrazit', 169.00, 1),
    (4, 'rot/anthrazit', 169.00, 1),
    (5, 'lemon', 196.00, 2),
    (6, '19-21 / S royalblau', 50.00, 3);

INSERT INTO product_inventory (id, count, product_variant_id, storage_id) VALUES
    (1, 5, 5, 1),
    (2, 4, 5, 2),
    (3, 2, 1, 1),
    (4, 4, 1, 2),
    (5, 3, 2, 1),
    (6, 1, 2, 2);
