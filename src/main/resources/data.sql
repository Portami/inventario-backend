-- Reference tables
INSERT IGNORE INTO felt_type (id, name) VALUES (1, 'Wollfilz');
INSERT IGNORE INTO felt_type (id, name) VALUES (2, 'Reinwollfilz');
INSERT IGNORE INTO felt_type (id, name) VALUES (3, 'Visco-Wollfilz');
INSERT IGNORE INTO felt_type (id, name) VALUES (4, 'Universalfilz');

INSERT IGNORE INTO supplier (id, name) VALUES (1, 'Wiler');
INSERT IGNORE INTO supplier (id, name) VALUES (2, 'Birki');
INSERT IGNORE INTO supplier (id, name) VALUES (3, 'M&K');
INSERT IGNORE INTO supplier (id, name) VALUES (4, 'VFG');
INSERT IGNORE INTO supplier (id, name) VALUES (5, 'Holland');
INSERT IGNORE INTO supplier (id, name) VALUES (6, 'Schafwoll');

INSERT IGNORE INTO storage (id, name) VALUES (1, 'Atelier');
INSERT IGNORE INTO storage (id, name) VALUES (2, 'Keller');

INSERT IGNORE INTO batch (id, name) VALUES (1, 'R7K9M2PL');
INSERT IGNORE INTO batch (id, name) VALUES (2, 'W3FX8NAQ');
INSERT IGNORE INTO batch (id, name) VALUES (3, 'J5BV4TKE');

-- Felt
INSERT IGNORE INTO felt(id, felt_type_id, supplier_id, article_number, thickness, density, price, color, supplier_color) VALUES (1, 1, 2, '1015/00', 1.2, 25, 100, 'zitronengelb', 'zitronengelb');




-- Scrap Piece (sample pieces for testing)
INSERT IGNORE INTO scrap_piece (id, felt_color_variant_id, batch_id, storage_id, length, width) VALUES (1, 1, 2, 1, 57.3, 58.7);

-- Barcode
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id) VALUES (1, 'ROLL', 1, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id) VALUES (2, 'ROLL', 2, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id) VALUES (11, 'SCRAP', NULL, 1);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id) VALUES (12, 'SCRAP', NULL, 2);

-- Supply
INSERT IGNORE INTO supply (id, is_low_on_supply, is_reordered) VALUES (1, false, false);
