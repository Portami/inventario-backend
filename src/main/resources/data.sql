-- Reference tables
INSERT IGNORE INTO felt_type (id, name)
VALUES (1, 'Wollfilz');
INSERT IGNORE INTO felt_type (id, name)
VALUES (2, 'Reinwollfilz');
INSERT IGNORE INTO felt_type (id, name)
VALUES (3, 'Visco-Wollfilz');
INSERT IGNORE INTO felt_type (id, name)
VALUES (4, 'Universalfilz');

INSERT IGNORE INTO supplier (id, name)
VALUES (1, 'Wiler');
INSERT IGNORE INTO supplier (id, name)
VALUES (2, 'Birki');
INSERT IGNORE INTO supplier (id, name)
VALUES (3, 'M&K');
INSERT IGNORE INTO supplier (id, name)
VALUES (4, 'VFG');
INSERT IGNORE INTO supplier (id, name)
VALUES (5, 'Holland');
INSERT IGNORE INTO supplier (id, name)
VALUES (6, 'Schafwoll');

INSERT IGNORE INTO storage (id, name)
VALUES (1, 'Atelier');
INSERT IGNORE INTO storage (id, name)
VALUES (2, 'Keller');

-- Batches (one per felt, 8-char alphanumeric names)
INSERT IGNORE INTO batch (id, name)
VALUES (1, 'R7K9M2PL');
INSERT IGNORE INTO batch (id, name)
VALUES (2, 'W3FX8NAQ');
INSERT IGNORE INTO batch (id, name)
VALUES (3, 'J5BV4TKE');
INSERT IGNORE INTO batch (id, name)
VALUES (4, 'T8HN3KPQ');
INSERT IGNORE INTO batch (id, name)
VALUES (5, 'V2LM7XRS');
INSERT IGNORE INTO batch (id, name)
VALUES (6, 'B9CW4FJD');
INSERT IGNORE INTO batch (id, name)
VALUES (7, 'N6YA1GEH');
INSERT IGNORE INTO batch (id, name)
VALUES (8, 'Q5DK8UZI');
INSERT IGNORE INTO batch (id, name)
VALUES (9, 'X3PF2WOL');
INSERT IGNORE INTO batch (id, name)
VALUES (10, 'H7RJ6SMN');
INSERT IGNORE INTO batch (id, name)
VALUES (11, 'C1GT9VBY');
INSERT IGNORE INTO batch (id, name)
VALUES (12, 'U4EX5ACK');
INSERT IGNORE INTO batch (id, name)
VALUES (13, 'L8WQ3DFP');
INSERT IGNORE INTO batch (id, name)
VALUES (14, 'A6NM7RZJ');
INSERT IGNORE INTO batch (id, name)
VALUES (15, 'F2KS9HTG');
INSERT IGNORE INTO batch (id, name)
VALUES (16, 'P5YB1CWV');
INSERT IGNORE INTO batch (id, name)
VALUES (17, 'D9XL4ENQ');
INSERT IGNORE INTO batch (id, name)
VALUES (18, 'M3RA8JKU');
INSERT IGNORE INTO batch (id, name)
VALUES (19, 'Z7FH2GOT');
INSERT IGNORE INTO batch (id, name)
VALUES (20, 'S1PN6BWC');
INSERT IGNORE INTO batch (id, name)
VALUES (21, 'G4TX9EAI');
INSERT IGNORE INTO batch (id, name)
VALUES (22, 'J8VD5KLR');
INSERT IGNORE INTO batch (id, name)
VALUES (23, 'W6CM3HYF');
INSERT IGNORE INTO batch (id, name)
VALUES (24, 'E2QN7PAB');
INSERT IGNORE INTO batch (id, name)
VALUES (25, 'K9GJ4TUX');
INSERT IGNORE INTO batch (id, name)
VALUES (26, 'R5HL1WSD');
INSERT IGNORE INTO batch (id, name)
VALUES (27, 'I3YC8FMA');
INSERT IGNORE INTO batch (id, name)
VALUES (28, 'O7BX2KNE');
INSERT IGNORE INTO batch (id, name)
VALUES (29, 'Y1DQ6GPJ');
INSERT IGNORE INTO batch (id, name)
VALUES (30, 'T4SM9RVL');
INSERT IGNORE INTO batch (id, name)
VALUES (31, 'H8AF3CWU');
INSERT IGNORE INTO batch (id, name)
VALUES (32, 'N2EK5XBZ');
INSERT IGNORE INTO batch (id, name)
VALUES (33, 'Q6JP7TDG');
INSERT IGNORE INTO batch (id, name)
VALUES (34, 'V9WR1MHI');
INSERT IGNORE INTO batch (id, name)
VALUES (35, 'B3LN8YAF');
INSERT IGNORE INTO batch (id, name)
VALUES (36, 'X5CT2QEK');
INSERT IGNORE INTO batch (id, name)
VALUES (37, 'F7DA4SJP');
INSERT IGNORE INTO batch (id, name)
VALUES (38, 'X3DA9PJP');

-- Felt (all felt_type_id=1 Wollfilz, supplier_id=2 Birki, price=0)
INSERT IGNORE INTO felt (id, felt_type_id, supplier_id, article_number, thickness, density, price, color,
                         supplier_color)
VALUES (1, 1, 2, '1015/00', 1.2, 25, 0, 'zitronengelb', 'zitronengelb');
INSERT IGNORE INTO felt (id, felt_type_id, supplier_id, article_number, thickness, density, price, color,
                         supplier_color)
VALUES (2, 1, 2, '1016/01', 1.2, 25, 0, 'gelb', 'gelb');
INSERT IGNORE INTO felt (id, felt_type_id, supplier_id, article_number, thickness, density, price, color,
                         supplier_color)
VALUES (3, 1, 2, '1020/02', 1.2, 25, 0, 'orange', 'orange');
INSERT IGNORE INTO felt (id, felt_type_id, supplier_id, article_number, thickness, density, price, color,
                         supplier_color)
VALUES (4, 1, 2, '1083/03', 1.2, 25, 0, 'dunkelrot', 'dunkelrot');
INSERT IGNORE INTO felt (id, felt_type_id, supplier_id, article_number, thickness, density, price, color,
                         supplier_color)
VALUES (5, 1, 2, '1007/04', 1.2, 25, 0, 'aubergin', 'aubergin');
INSERT IGNORE INTO felt (id, felt_type_id, supplier_id, article_number, thickness, density, price, color,
                         supplier_color)
VALUES (6, 1, 2, '1088/05', 1.2, 25, 0, 'rosa', 'rosa');
INSERT IGNORE INTO felt (id, felt_type_id, supplier_id, article_number, thickness, density, price, color,
                         supplier_color)
VALUES (7, 1, 2, '1073/06', 1.2, 25, 0, 'pink', 'pink');
INSERT IGNORE INTO felt (id, felt_type_id, supplier_id, article_number, thickness, density, price, color,
                         supplier_color)
VALUES (8, 1, 2, '1012/07', 1.2, 25, 0, 'kirschrot', 'kirschrot');
INSERT IGNORE INTO felt (id, felt_type_id, supplier_id, article_number, thickness, density, price, color,
                         supplier_color)
VALUES (9, 1, 2, '2005/08', 1.2, 25, 0, 'lila', 'lila');
INSERT IGNORE INTO felt (id, felt_type_id, supplier_id, article_number, thickness, density, price, color,
                         supplier_color)
VALUES (10, 1, 2, '1010/09', 1.2, 25, 0, 'bordeaux', 'bordeaux');
INSERT IGNORE INTO felt (id, felt_type_id, supplier_id, article_number, thickness, density, price, color,
                         supplier_color)
VALUES (11, 1, 2, '1065/10', 1.2, 25, 0, 'grafitgrau', 'grafitgrau');
INSERT IGNORE INTO felt (id, felt_type_id, supplier_id, article_number, thickness, density, price, color,
                         supplier_color)
VALUES (12, 1, 2, '1067/11', 1.2, 25, 0, 'schwarz', 'schwarz');
INSERT IGNORE INTO felt (id, felt_type_id, supplier_id, article_number, thickness, density, price, color,
                         supplier_color)
VALUES (13, 1, 2, '1049/12', 1.2, 25, 0, 'dunkelbraun', 'dunkelbraun');
INSERT IGNORE INTO felt (id, felt_type_id, supplier_id, article_number, thickness, density, price, color,
                         supplier_color)
VALUES (14, 1, 2, '1014/13', 1.2, 25, 0, 'tannengrün', 'tannengrün');
INSERT IGNORE INTO felt (id, felt_type_id, supplier_id, article_number, thickness, density, price, color,
                         supplier_color)
VALUES (15, 1, 2, '1063/14', 1.2, 25, 0, 'olive', 'olive');
INSERT IGNORE INTO felt (id, felt_type_id, supplier_id, article_number, thickness, density, price, color,
                         supplier_color)
VALUES (16, 1, 2, '1029/15', 1.2, 25, 0, 'pistazie', 'pistazie');
INSERT IGNORE INTO felt (id, felt_type_id, supplier_id, article_number, thickness, density, price, color,
                         supplier_color)
VALUES (17, 1, 2, '1024/16', 1.2, 25, 0, 'apfelgrün', 'apfelgrün');
INSERT IGNORE INTO felt (id, felt_type_id, supplier_id, article_number, thickness, density, price, color,
                         supplier_color)
VALUES (18, 1, 2, '1093/17', 1.2, 25, 0, 'rohweiss', 'rohweiss');
INSERT IGNORE INTO felt (id, felt_type_id, supplier_id, article_number, thickness, density, price, color,
                         supplier_color)
VALUES (19, 1, 2, '1042/18', 1.2, 25, 0, 'weiss', 'weiss');
INSERT IGNORE INTO felt (id, felt_type_id, supplier_id, article_number, thickness, density, price, color,
                         supplier_color)
VALUES (20, 1, 2, '1087/19', 1.2, 25, 0, 'ice', 'ice');
INSERT IGNORE INTO felt (id, felt_type_id, supplier_id, article_number, thickness, density, price, color,
                         supplier_color)
VALUES (21, 1, 2, '1037/20', 1.2, 25, 0, 'blau', 'blau');
INSERT IGNORE INTO felt (id, felt_type_id, supplier_id, article_number, thickness, density, price, color,
                         supplier_color)
VALUES (22, 1, 2, '1036/21', 1.2, 25, 0, 'hellblau', 'hellblau');
INSERT IGNORE INTO felt (id, felt_type_id, supplier_id, article_number, thickness, density, price, color,
                         supplier_color)
VALUES (23, 1, 2, '1039/23', 1.2, 25, 0, 'royalblau', 'royalblau');
INSERT IGNORE INTO felt (id, felt_type_id, supplier_id, article_number, thickness, density, price, color,
                         supplier_color)
VALUES (24, 1, 2, '1058/24', 1.2, 25, 0, 'türkis', 'türkis');
INSERT IGNORE INTO felt (id, felt_type_id, supplier_id, article_number, thickness, density, price, color,
                         supplier_color)
VALUES (25, 1, 2, '1076/25', 1.2, 25, 0, 'violett', 'violett');
INSERT IGNORE INTO felt (id, felt_type_id, supplier_id, article_number, thickness, density, price, color,
                         supplier_color)
VALUES (26, 1, 2, '10G8/01', 1.2, 25, 0, 'dunkelgrau-meliert', 'dunkelgrau-meliert');
INSERT IGNORE INTO felt (id, felt_type_id, supplier_id, article_number, thickness, density, price, color,
                         supplier_color)
VALUES (27, 1, 2, '10G6/02', 1.2, 25, 0, 'hellgrau-meliert', 'hellgrau-meliert');
INSERT IGNORE INTO felt (id, felt_type_id, supplier_id, article_number, thickness, density, price, color,
                         supplier_color)
VALUES (28, 1, 2, '10G9/03', 1.2, 25, 0, 'anthrazit-meliert', 'anthrazit-meliert');
INSERT IGNORE INTO felt (id, felt_type_id, supplier_id, article_number, thickness, density, price, color,
                         supplier_color)
VALUES (29, 1, 2, '10G3/04', 1.2, 25, 0, 'dunkelbraun-meliert', 'dunkelbraun-meliert');
INSERT IGNORE INTO felt (id, felt_type_id, supplier_id, article_number, thickness, density, price, color,
                         supplier_color)
VALUES (30, 1, 2, '10G0/05', 1.2, 25, 0, 'hellbeige-meliert', 'hellbeige-meliert');
INSERT IGNORE INTO felt (id, felt_type_id, supplier_id, article_number, thickness, density, price, color,
                         supplier_color)
VALUES (31, 1, 2, 'G1AH', 1.2, 25, 0, 'camel-meliert / Streifen', 'camel-meliert / Streifen');
INSERT IGNORE INTO felt (id, felt_type_id, supplier_id, article_number, thickness, density, price, color,
                         supplier_color)
VALUES (32, 1, 2, '10G20/06', 1.2, 25, 0, 'rost meliert', 'rost - meliert');
INSERT IGNORE INTO felt (id, felt_type_id, supplier_id, article_number, thickness, density, price, color,
                         supplier_color)
VALUES (33, 1, 2, '10G16/07', 1.2, 25, 0, 'gelb-meliert', 'gelb-meliert');
INSERT IGNORE INTO felt (id, felt_type_id, supplier_id, article_number, thickness, density, price, color,
                         supplier_color)
VALUES (34, 1, 2, 'MF 25', 2, 25, 0, 'pistazie', 'pistazie');
INSERT IGNORE INTO felt (id, felt_type_id, supplier_id, article_number, thickness, density, price, color,
                         supplier_color)
VALUES (35, 2, 5, '2046', 2, 25, 0, 'billard grün', 'billard grün');

-- Felt Rolls (width=180 + length varied, or length=180 + width 95-105)
-- Felt 1 (zitronengelb) - batch 1
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (1, 1000, 180, 1, 1, 2);
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (2, 180, 100, 1, 1, 2);
-- Felt 2 (gelb) - batch 4
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (3, 850, 180, 4, 2, 2);
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (4, 180, 98, 4, 2, 2);
-- Felt 3 (orange) - batch 5
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (5, 1200, 180, 5, 3, 2);
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (6, 180, 102, 5, 3, 2);
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (7, 180, 97, 5, 3, 2);
-- Felt 4 (dunkelrot) - batch 6
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (8, 380, 180, 6, 4, 2);
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (9, 180, 103, 6, 4, 2);
-- Felt 5 (aubergin) - batch 7
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (10, 900, 180, 7, 5, 2);
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (11, 180, 99, 7, 5, 2);
-- Felt 6 (rosa) - batch 8
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (12, 1350, 180, 8, 6, 2);
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (13, 180, 101, 8, 6, 2);
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (14, 180, 95, 8, 6, 2);
-- Felt 7 (pink) - batch 9
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (15, 600, 180, 9, 7, 2);
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (16, 180, 105, 9, 7, 2);
-- Felt 8 (kirschrot) - batch 10
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (17, 1480, 180, 10, 8, 2);
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (18, 180, 96, 10, 8, 2);
-- Felt 9 (lila) - batch 11
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (19, 530, 180, 11, 9, 2);
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (20, 180, 104, 11, 9, 2);
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (21, 180, 100, 11, 9, 2);
-- Felt 10 (bordeaux) - batch 12
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (22, 800, 180, 12, 10, 2);
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (23, 180, 98, 12, 10, 2);
-- Felt 11 (grafitgrau) - batch 13
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (24, 1250, 180, 13, 11, 2);
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (25, 180, 103, 13, 11, 2);
-- Felt 12 (schwarz) - batch 14
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (26, 420, 180, 14, 12, 2);
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (27, 180, 97, 14, 12, 2);
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (28, 180, 102, 14, 12, 2);
-- Felt 13 (dunkelbraun) - batch 15
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (29, 950, 180, 15, 13, 2);
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (30, 180, 99, 15, 13, 2);
-- Felt 14 (tannengrün) - batch 16
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (31, 1150, 180, 16, 14, 2);
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (32, 180, 101, 16, 14, 2);
-- Felt 15 (olive) - batch 17
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (33, 670, 180, 17, 15, 2);
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (34, 180, 96, 17, 15, 2);
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (35, 180, 105, 17, 15, 2);
-- Felt 16 (pistazie) - batch 18
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (36, 1400, 180, 18, 16, 2);
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (37, 180, 95, 18, 16, 2);
-- Felt 17 (apfelgrün) - batch 19
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (38, 350, 180, 19, 17, 2);
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (39, 180, 100, 19, 17, 2);
-- Felt 18 (rohweiss) - batch 20
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (40, 780, 180, 20, 18, 2);
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (41, 180, 98, 20, 18, 2);
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (42, 180, 104, 20, 18, 2);
-- Felt 19 (weiss) - batch 21
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (43, 1050, 180, 21, 19, 2);
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (44, 180, 103, 21, 19, 2);
-- Felt 20 (ice) - batch 22
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (45, 500, 180, 22, 20, 2);
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (46, 180, 97, 22, 20, 2);
-- Felt 21 (blau) - batch 23
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (47, 1300, 180, 23, 21, 2);
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (48, 180, 102, 23, 21, 2);
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (49, 180, 99, 23, 21, 2);
-- Felt 22 (hellblau) - batch 24
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (50, 650, 180, 24, 22, 2);
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (51, 180, 101, 24, 22, 2);
-- Felt 23 (royalblau) - batch 25
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (52, 880, 180, 25, 23, 2);
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (53, 180, 96, 25, 23, 2);
-- Felt 24 (türkis) - batch 26
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (54, 1420, 180, 26, 24, 2);
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (55, 180, 105, 26, 24, 2);
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (56, 180, 95, 26, 24, 2);
-- Felt 25 (violett) - batch 27
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (57, 550, 180, 27, 25, 2);
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (58, 180, 100, 27, 25, 2);
-- Felt 26 (dunkelgrau-meliert) - batch 28
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (59, 740, 180, 28, 26, 2);
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (60, 180, 98, 28, 26, 2);
-- Felt 27 (hellgrau-meliert) - batch 29
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (61, 1180, 180, 29, 27, 2);
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (62, 180, 103, 29, 27, 2);
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (63, 180, 97, 29, 27, 2);
-- Felt 28 (anthrazit-meliert) - batch 30
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (64, 400, 180, 30, 28, 2);
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (65, 180, 102, 30, 28, 2);
-- Felt 29 (dunkelbraun-meliert) - batch 31
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (66, 980, 180, 31, 29, 2);
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (67, 180, 99, 31, 29, 2);
-- Felt 30 (hellbeige-meliert) - batch 32
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (68, 1280, 180, 32, 30, 2);
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (69, 180, 101, 32, 30, 2);
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (70, 180, 96, 32, 30, 2);
-- Felt 31 (camel-meliert / Streifen) - batch 33
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (71, 620, 180, 33, 31, 2);
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (72, 180, 105, 33, 31, 2);
-- Felt 32 (rost meliert) - batch 34
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (73, 850, 180, 34, 32, 2);
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (74, 180, 95, 34, 32, 2);
-- Felt 33 (gelb-meliert) - batch 35
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (75, 700, 180, 35, 33, 2);
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (76, 180, 100, 35, 33, 2);
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (77, 180, 98, 35, 33, 2);
-- Felt 34 (MF 25 pistazie) - batch 36
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (78, 1100, 180, 36, 34, 2);
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (79, 180, 104, 36, 34, 2);
-- Felt 35
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (80, 1300, 180, 38, 35, 1);
INSERT IGNORE INTO felt_roll (id, length, width, batch_id, felt_id, storage_id)
VALUES (81, 180, 100, 38, 35, 1);

-- Scrap Piece (sample pieces for testing)
INSERT IGNORE INTO scrap_piece (id, felt_id, batch_id, storage_id, length, width)
VALUES (1, 1, 2, 1, 57.3, 58.7);
INSERT IGNORE INTO scrap_piece (id, felt_id, batch_id, storage_id, length, width)
VALUES (2, 35, 38, 1, 60.3, 46);

-- Barcode
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (1, 'ROLL', 1, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (2, 'ROLL', 2, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (3, 'ROLL', 3, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (4, 'ROLL', 4, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (5, 'ROLL', 5, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (6, 'ROLL', 6, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (7, 'ROLL', 7, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (8, 'ROLL', 8, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (9, 'ROLL', 9, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (10, 'ROLL', 10, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (11, 'ROLL', 11, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (12, 'ROLL', 12, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (13, 'ROLL', 13, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (14, 'ROLL', 14, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (15, 'ROLL', 15, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (16, 'ROLL', 16, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (17, 'ROLL', 17, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (18, 'ROLL', 18, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (19, 'ROLL', 19, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (20, 'ROLL', 20, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (21, 'ROLL', 21, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (22, 'ROLL', 22, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (23, 'ROLL', 23, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (24, 'ROLL', 24, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (25, 'ROLL', 25, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (26, 'ROLL', 26, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (27, 'ROLL', 27, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (28, 'ROLL', 28, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (29, 'ROLL', 29, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (30, 'ROLL', 30, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (31, 'ROLL', 31, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (32, 'ROLL', 32, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (33, 'ROLL', 33, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (34, 'ROLL', 34, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (35, 'ROLL', 35, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (36, 'ROLL', 36, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (37, 'ROLL', 37, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (38, 'ROLL', 38, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (39, 'ROLL', 39, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (40, 'ROLL', 40, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (41, 'ROLL', 41, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (42, 'ROLL', 42, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (43, 'ROLL', 43, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (44, 'ROLL', 44, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (45, 'ROLL', 45, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (46, 'ROLL', 46, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (47, 'ROLL', 47, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (48, 'ROLL', 48, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (49, 'ROLL', 49, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (50, 'ROLL', 50, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (51, 'ROLL', 51, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (52, 'ROLL', 52, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (53, 'ROLL', 53, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (54, 'ROLL', 54, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (55, 'ROLL', 55, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (56, 'ROLL', 56, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (57, 'ROLL', 57, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (58, 'ROLL', 58, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (59, 'ROLL', 59, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (60, 'ROLL', 60, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (61, 'ROLL', 61, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (62, 'ROLL', 62, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (63, 'ROLL', 63, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (64, 'ROLL', 64, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (65, 'ROLL', 65, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (66, 'ROLL', 66, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (67, 'ROLL', 67, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (68, 'ROLL', 68, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (69, 'ROLL', 69, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (70, 'ROLL', 70, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (71, 'ROLL', 71, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (72, 'ROLL', 72, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (73, 'ROLL', 73, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (74, 'ROLL', 74, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (75, 'ROLL', 75, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (76, 'ROLL', 76, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (77, 'ROLL', 77, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (78, 'ROLL', 78, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (79, 'ROLL', 79, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (80, 'ROLL', 80, NULL);
INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (81, 'ROLL', 81, NULL);

INSERT IGNORE INTO barcode (id, type, felt_roll_id, scrap_piece_id)
VALUES (82, 'SCRAP', NULL, 1);


INSERT IGNORE INTO category (id, name)
VALUES (1, 'Taschen');
INSERT IGNORE INTO category (id, name)
VALUES (2, 'Kinderfinken');

INSERT IGNORE INTO product (id, name, category_id)
VALUES (1, 'shopper-1', 1);
INSERT IGNORE INTO product (id, name, category_id)
VALUES (2, 'shopper-2', 1);
INSERT IGNORE INTO product (id, name, category_id)
VALUES (3, 'täppali', 2);
INSERT IGNORE INTO product_variant (id, name, price, product_id)
VALUES (1, 'weiss/schwarz', 169.00, 1);

INSERT IGNORE INTO product_variant (id, name, price, product_id)
VALUES (2, 'giftgrün/grau', 169.00, 1);
INSERT IGNORE INTO product_variant (id, name, price, product_id)
VALUES (3, 'petrol/anthrazit', 169.00, 1);
INSERT IGNORE INTO product_variant (id, name, price, product_id)
VALUES (4, 'rot/anthrazit', 169.00, 1);
INSERT IGNORE INTO product_variant (id, name, price, product_id)
VALUES (5, 'lemon', 196.00, 2);
INSERT IGNORE INTO product_variant (id, name, price, product_id)
VALUES (6, '19-21 / S royalblau', 50.00, 3);

INSERT IGNORE INTO product_inventory (id, count, product_variant_id, storage_id)
VALUES (1, 5, 5, 1);
INSERT IGNORE INTO product_inventory (id, count, product_variant_id, storage_id)
VALUES (2, 4, 5, 2);
INSERT IGNORE INTO product_inventory (id, count, product_variant_id, storage_id)
VALUES (3, 2, 1, 1);
INSERT IGNORE INTO product_inventory (id, count, product_variant_id, storage_id)
VALUES (4, 4, 1, 2);
INSERT IGNORE INTO product_inventory (id, count, product_variant_id, storage_id)
VALUES (5, 3, 2, 1);
INSERT IGNORE INTO product_inventory (id, count, product_variant_id, storage_id)
VALUES (6, 1, 2, 2);

-- ============================================================
-- OFFER TEST DATA
-- ============================================================

-- Customers (offer_customer)
-- 1 Atelier Madeleine Schönberg — top customer, very reliable
INSERT IGNORE INTO offer_customer (id, name, contact_person, email, phone, street, zip, city, country, vat_number)
VALUES (1, 'Atelier Madeleine Schönberg', 'Madeleine Schönberg', 'm.schoenberg@atelier-ms.ch', '+41 44 210 33 55', 'Limmatquai 18', '8001', 'Zürich', 'CH', 'CHE-123.456.789 MWST');

-- 2 Textile Werkstatt Brunner GmbH — reliable, mid-size orders
INSERT IGNORE INTO offer_customer (id, name, contact_person, email, phone, street, zip, city, country, vat_number)
VALUES (2, 'Textile Werkstatt Brunner GmbH', 'Peter Brunner', 'p.brunner@textilwerkstatt.ch', '+41 31 372 88 10', 'Aarbergergasse 7', '3011', 'Bern', 'CH', 'CHE-234.567.890 MWST');

-- 3 Kreativ-Markt Luzern AG — good but has one overdue invoice
INSERT IGNORE INTO offer_customer (id, name, contact_person, email, phone, street, zip, city, country, vat_number)
VALUES (3, 'Kreativ-Markt Luzern AG', 'Sandra Kälin', 's.kaelin@kreativmarkt-luzern.ch', '+41 41 420 05 70', 'Hertensteinstrasse 22', '6004', 'Luzern', 'CH', 'CHE-345.678.901 MWST');

-- 4 Handwerk & Design Studio Huber — mixed, has first dunning notice
INSERT IGNORE INTO offer_customer (id, name, contact_person, email, phone, street, zip, city, country, vat_number)
VALUES (4, 'Handwerk & Design Studio Huber', 'Jonas Huber', 'j.huber@hd-studio.ch', '+41 61 381 22 40', 'Freie Strasse 12', '4001', 'Basel', 'CH', 'CHE-456.789.012 MWST');

-- 5 Filzkunst Atelier Müller — reliable, one fresh offer pending
INSERT IGNORE INTO offer_customer (id, name, contact_person, email, phone, street, zip, city, country, vat_number)
VALUES (5, 'Filzkunst Atelier Müller', 'Heidi Müller', 'h.mueller@filzkunst.ch', '+41 71 244 66 80', 'Gallusstrasse 14', '9000', 'St. Gallen', 'CH', 'CHE-567.890.123 MWST');

-- 6 Bastelbedarf Weber AG — unreliable, mostly lost/no response
INSERT IGNORE INTO offer_customer (id, name, contact_person, email, phone, street, zip, city, country, vat_number)
VALUES (6, 'Bastelbedarf Weber AG', 'Thomas Weber', 't.weber@bastelbedarf-weber.ch', '+41 52 212 77 30', 'Marktgasse 5', '8400', 'Winterthur', 'CH', 'CHE-678.901.234 MWST');

-- 7 Hobby-Treff Schneider GmbH — avoid: zero completions, all cancelled/no response
INSERT IGNORE INTO offer_customer (id, name, contact_person, email, phone, street, zip, city, country, vat_number)
VALUES (7, 'Hobby-Treff Schneider GmbH', 'Kurt Schneider', 'k.schneider@hobby-treff.ch', '+41 62 824 55 10', 'Rathausgasse 3', '5000', 'Aarau', 'CH', 'CHE-789.012.345 MWST');

-- 8 Kunsthandwerk Bachmann — medium, overdue payment reminder
INSERT IGNORE INTO offer_customer (id, name, contact_person, email, phone, street, zip, city, country, vat_number)
VALUES (8, 'Kunsthandwerk Bachmann', 'Ursula Bachmann', 'u.bachmann@kh-bachmann.ch', '+41 81 252 14 60', 'Reichsgasse 9', '7000', 'Chur', 'CH', 'CHE-890.123.456 MWST');

-- 9 Mode Atelier Keller — new customer, two offers in early stages
INSERT IGNORE INTO offer_customer (id, name, contact_person, email, phone, street, zip, city, country, vat_number)
VALUES (9, 'Mode Atelier Keller', 'Anna Keller', 'a.keller@modeatelier-keller.ch', '+41 62 296 38 90', 'Hauptgasse 16', '4600', 'Olten', 'CH', NULL);

-- 10 Stoff & Faden Huber — serious overdue second dunning notice
INSERT IGNORE INTO offer_customer (id, name, contact_person, email, phone, street, zip, city, country, vat_number)
VALUES (10, 'Stoff & Faden Huber', 'Markus Huber', 'm.huber@stoff-faden.ch', '+41 52 721 09 50', 'Zürcherstrasse 28', '8500', 'Frauenfeld', 'CH', 'CHE-901.234.567 MWST');

-- ============================================================
-- Offers
-- Customer 1 (Atelier Madeleine Schönberg) — 5 COMPLETED + 1 INVOICE
-- ============================================================
INSERT IGNORE INTO offer (id, customer_id, state, created_at, updated_at, due_at, offer_sent)
VALUES (1,  1, 'COMPLETED', '2025-03-10 09:15:00', '2025-04-02 14:30:00', '2025-03-24 00:00:00', true);
INSERT IGNORE INTO offer (id, customer_id, state, created_at, updated_at, due_at, offer_sent)
VALUES (2,  1, 'COMPLETED', '2025-05-20 10:00:00', '2025-06-18 11:45:00', '2025-06-10 00:00:00', true);
INSERT IGNORE INTO offer (id, customer_id, state, created_at, updated_at, due_at, offer_sent)
VALUES (3,  1, 'COMPLETED', '2025-08-12 08:30:00', '2025-09-05 16:00:00', '2025-08-26 00:00:00', true);
INSERT IGNORE INTO offer (id, customer_id, state, created_at, updated_at, due_at, offer_sent)
VALUES (4,  1, 'COMPLETED', '2025-11-05 09:00:00', '2025-12-02 13:20:00', '2025-11-19 00:00:00', true);
INSERT IGNORE INTO offer (id, customer_id, state, created_at, updated_at, due_at, offer_sent)
VALUES (5,  1, 'COMPLETED', '2026-02-18 10:30:00', '2026-03-12 15:10:00', '2026-03-04 00:00:00', true);
INSERT IGNORE INTO offer (id, customer_id, state, created_at, updated_at, due_at, offer_sent)
VALUES (6,  1, 'INVOICE',   '2026-04-22 11:00:00', '2026-05-08 09:30:00', '2026-06-20 00:00:00', true);

-- Customer 2 (Textile Werkstatt Brunner GmbH) — 4 COMPLETED + 1 ORDER_CONFIRMATION
INSERT IGNORE INTO offer (id, customer_id, state, created_at, updated_at, due_at, offer_sent)
VALUES (7,  2, 'COMPLETED',          '2025-04-08 08:45:00', '2025-05-14 10:00:00', '2025-04-28 00:00:00', true);
INSERT IGNORE INTO offer (id, customer_id, state, created_at, updated_at, due_at, offer_sent)
VALUES (8,  2, 'COMPLETED',          '2025-07-14 09:30:00', '2025-08-20 14:00:00', '2025-08-05 00:00:00', true);
INSERT IGNORE INTO offer (id, customer_id, state, created_at, updated_at, due_at, offer_sent)
VALUES (9,  2, 'COMPLETED',          '2025-10-22 10:15:00', '2025-11-28 11:30:00', '2025-11-11 00:00:00', true);
INSERT IGNORE INTO offer (id, customer_id, state, created_at, updated_at, due_at, offer_sent)
VALUES (10, 2, 'COMPLETED',          '2026-01-30 08:00:00', '2026-03-05 09:45:00', '2026-02-13 00:00:00', true);
INSERT IGNORE INTO offer (id, customer_id, state, created_at, updated_at, due_at, offer_sent)
VALUES (11, 2, 'ORDER_CONFIRMATION', '2026-05-15 11:30:00', '2026-05-22 10:00:00', '2026-07-10 00:00:00', true);

-- Customer 3 (Kreativ-Markt Luzern AG) — 3 COMPLETED + 1 PAYMENT_REMINDER (overdue)
INSERT IGNORE INTO offer (id, customer_id, state, created_at, updated_at, due_at, offer_sent)
VALUES (12, 3, 'COMPLETED',          '2025-05-14 09:00:00', '2025-06-20 16:00:00', '2025-06-04 00:00:00', true);
INSERT IGNORE INTO offer (id, customer_id, state, created_at, updated_at, due_at, offer_sent)
VALUES (13, 3, 'COMPLETED',          '2025-09-03 10:30:00', '2025-10-10 13:00:00', '2025-09-24 00:00:00', true);
INSERT IGNORE INTO offer (id, customer_id, state, created_at, updated_at, due_at, offer_sent)
VALUES (14, 3, 'COMPLETED',          '2026-01-17 08:15:00', '2026-02-22 11:00:00', '2026-02-06 00:00:00', true);
INSERT IGNORE INTO offer (id, customer_id, state, created_at, updated_at, due_at, offer_sent)
VALUES (15, 3, 'PAYMENT_REMINDER',   '2026-02-28 09:00:00', '2026-04-02 10:15:00', '2026-04-15 00:00:00', true);

-- Customer 4 (Handwerk & Design Studio Huber) — 3 COMPLETED + 1 FIRST_DUNNING_NOTICE (overdue)
INSERT IGNORE INTO offer (id, customer_id, state, created_at, updated_at, due_at, offer_sent)
VALUES (16, 4, 'COMPLETED',             '2025-06-19 10:00:00', '2025-07-22 14:30:00', '2025-07-09 00:00:00', true);
INSERT IGNORE INTO offer (id, customer_id, state, created_at, updated_at, due_at, offer_sent)
VALUES (17, 4, 'COMPLETED',             '2025-10-08 09:15:00', '2025-11-15 12:00:00', '2025-10-29 00:00:00', true);
INSERT IGNORE INTO offer (id, customer_id, state, created_at, updated_at, due_at, offer_sent)
VALUES (18, 4, 'COMPLETED',             '2026-01-25 10:30:00', '2026-02-28 15:00:00', '2026-02-15 00:00:00', true);
INSERT IGNORE INTO offer (id, customer_id, state, created_at, updated_at, due_at, offer_sent)
VALUES (19, 4, 'FIRST_DUNNING_NOTICE', '2025-12-10 08:00:00', '2026-03-15 09:00:00', '2026-02-28 00:00:00', true);

-- Customer 5 (Filzkunst Atelier Müller) — 3 COMPLETED + 1 fresh OFFER
INSERT IGNORE INTO offer (id, customer_id, state, created_at, updated_at, due_at, offer_sent)
VALUES (20, 5, 'COMPLETED', '2025-05-30 09:30:00', '2025-06-30 11:00:00', '2025-06-20 00:00:00', true);
INSERT IGNORE INTO offer (id, customer_id, state, created_at, updated_at, due_at, offer_sent)
VALUES (21, 5, 'COMPLETED', '2025-09-18 10:00:00', '2025-10-20 14:30:00', '2025-10-08 00:00:00', true);
INSERT IGNORE INTO offer (id, customer_id, state, created_at, updated_at, due_at, offer_sent)
VALUES (22, 5, 'COMPLETED', '2026-02-05 08:45:00', '2026-03-07 10:15:00', '2026-02-26 00:00:00', true);
INSERT IGNORE INTO offer (id, customer_id, state, created_at, updated_at, due_at, offer_sent)
VALUES (23, 5, 'OFFER',     '2026-05-28 11:00:00', '2026-05-28 11:00:00', '2026-06-11 00:00:00', false);

-- Customer 6 (Bastelbedarf Weber AG) — 1 COMPLETED, 2 CANCELLED, 2 NO_RESPONSE
INSERT IGNORE INTO offer (id, customer_id, state, created_at, updated_at, due_at, offer_sent)
VALUES (24, 6, 'COMPLETED',   '2025-04-01 10:00:00', '2025-05-08 13:00:00', '2025-04-22 00:00:00', true);
INSERT IGNORE INTO offer (id, customer_id, state, created_at, updated_at, due_at, offer_sent)
VALUES (25, 6, 'CANCELLED',   '2025-06-10 09:00:00', '2025-07-15 10:30:00', '2025-06-24 00:00:00', true);
INSERT IGNORE INTO offer (id, customer_id, state, created_at, updated_at, due_at, offer_sent)
VALUES (26, 6, 'CANCELLED',   '2025-10-15 08:30:00', '2025-11-18 09:45:00', '2025-10-29 00:00:00', true);
INSERT IGNORE INTO offer (id, customer_id, state, created_at, updated_at, due_at, offer_sent)
VALUES (27, 6, 'NO_RESPONSE', '2025-12-05 10:15:00', '2026-01-20 11:00:00', '2025-12-19 00:00:00', true);
INSERT IGNORE INTO offer (id, customer_id, state, created_at, updated_at, due_at, offer_sent)
VALUES (28, 6, 'NO_RESPONSE', '2026-03-20 09:00:00', '2026-04-28 14:00:00', '2026-04-03 00:00:00', true);

-- Customer 7 (Hobby-Treff Schneider GmbH) — 2 CANCELLED, 2 NO_RESPONSE (avoid)
INSERT IGNORE INTO offer (id, customer_id, state, created_at, updated_at, due_at, offer_sent)
VALUES (29, 7, 'CANCELLED',   '2025-07-22 09:30:00', '2025-08-20 10:00:00', '2025-08-05 00:00:00', true);
INSERT IGNORE INTO offer (id, customer_id, state, created_at, updated_at, due_at, offer_sent)
VALUES (30, 7, 'CANCELLED',   '2025-11-11 10:00:00', '2025-12-10 11:30:00', '2025-11-25 00:00:00', true);
INSERT IGNORE INTO offer (id, customer_id, state, created_at, updated_at, due_at, offer_sent)
VALUES (31, 7, 'NO_RESPONSE', '2026-01-08 08:15:00', '2026-02-15 09:00:00', '2026-01-22 00:00:00', true);
INSERT IGNORE INTO offer (id, customer_id, state, created_at, updated_at, due_at, offer_sent)
VALUES (32, 7, 'NO_RESPONSE', '2026-04-03 11:00:00', '2026-05-12 14:30:00', '2026-04-17 00:00:00', true);

-- Customer 8 (Kunsthandwerk Bachmann) — 2 COMPLETED + 1 PAYMENT_REMINDER (overdue)
INSERT IGNORE INTO offer (id, customer_id, state, created_at, updated_at, due_at, offer_sent)
VALUES (33, 8, 'COMPLETED',        '2025-08-25 09:00:00', '2025-09-30 12:00:00', '2025-09-15 00:00:00', true);
INSERT IGNORE INTO offer (id, customer_id, state, created_at, updated_at, due_at, offer_sent)
VALUES (34, 8, 'COMPLETED',        '2025-12-15 10:30:00', '2026-01-20 14:00:00', '2026-01-05 00:00:00', true);
INSERT IGNORE INTO offer (id, customer_id, state, created_at, updated_at, due_at, offer_sent)
VALUES (35, 8, 'PAYMENT_REMINDER', '2026-03-05 08:45:00', '2026-04-10 10:00:00', '2026-04-20 00:00:00', true);

-- Customer 9 (Mode Atelier Keller) — 1 OFFER + 1 ORDER_CONFIRMATION
INSERT IGNORE INTO offer (id, customer_id, state, created_at, updated_at, due_at, offer_sent)
VALUES (36, 9, 'OFFER',              '2026-05-20 10:00:00', '2026-05-20 10:00:00', '2026-06-03 00:00:00', false);
INSERT IGNORE INTO offer (id, customer_id, state, created_at, updated_at, due_at, offer_sent)
VALUES (37, 9, 'ORDER_CONFIRMATION', '2026-04-28 09:15:00', '2026-05-05 11:00:00', '2026-06-25 00:00:00', true);

-- Customer 10 (Stoff & Faden Huber) — 2 COMPLETED + 1 SECOND_DUNNING_NOTICE (serious overdue)
INSERT IGNORE INTO offer (id, customer_id, state, created_at, updated_at, due_at, offer_sent)
VALUES (38, 10, 'COMPLETED',             '2025-07-30 08:30:00', '2025-08-28 13:00:00', '2025-08-20 00:00:00', true);
INSERT IGNORE INTO offer (id, customer_id, state, created_at, updated_at, due_at, offer_sent)
VALUES (39, 10, 'COMPLETED',             '2025-11-20 10:00:00', '2025-12-22 15:00:00', '2025-12-10 00:00:00', true);
INSERT IGNORE INTO offer (id, customer_id, state, created_at, updated_at, due_at, offer_sent)
VALUES (40, 10, 'SECOND_DUNNING_NOTICE', '2025-12-20 09:00:00', '2026-04-15 10:30:00', '2026-02-10 00:00:00', true);

-- ============================================================
-- Offer Items  (unit_price × quantity = total for each item)
-- ============================================================

-- Offer 1 (Atelier Schönberg, COMPLETED) — CHF 340
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (1,  1, NULL, 'Wollfilz schwarz 1.2mm, 50×80cm', 2, 85.00, 170.00, '2025-03-10 09:15:00', '2025-03-10 09:15:00');
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (2,  1, NULL, 'Wollfilz rohweiss 1.2mm, 100×180cm', 1, 170.00, 170.00, '2025-03-10 09:15:00', '2025-03-10 09:15:00');

-- Offer 2 (Atelier Schönberg, COMPLETED) — CHF 520
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (3,  2, NULL, 'Wollfilz grafitgrau 1.2mm, 100×180cm', 2, 145.00, 290.00, '2025-05-20 10:00:00', '2025-05-20 10:00:00');
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (4,  2, NULL, 'Wollfilz orange 1.2mm, 50×80cm', 1, 85.00, 85.00, '2025-05-20 10:00:00', '2025-05-20 10:00:00');
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (5,  2, 6,    'Täppali 19-21 / S royalblau', 3, 50.00, 150.00, '2025-05-20 10:00:00', '2025-05-20 10:00:00');

-- Offer 3 (Atelier Schönberg, COMPLETED) — CHF 280
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (6,  3, NULL, 'Wollfilz tannengrün 1.2mm, 100×180cm', 1, 155.00, 155.00, '2025-08-12 08:30:00', '2025-08-12 08:30:00');
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (7,  3, NULL, 'Reststück aubergin, 58×62cm', 1, 125.00, 125.00, '2025-08-12 08:30:00', '2025-08-12 08:30:00');

-- Offer 4 (Atelier Schönberg, COMPLETED) — CHF 650
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (8,  4, NULL, 'Wollfilz anthrazit-meliert 1.2mm, 200×180cm', 1, 295.00, 295.00, '2025-11-05 09:00:00', '2025-11-05 09:00:00');
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (9,  4, 1,    'Shopper weiss/schwarz', 2, 169.00, 338.00, '2025-11-05 09:00:00', '2025-11-05 09:00:00');
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (10, 4, NULL, 'Filzzuschnitt bordeaux, 30×60cm', 1, 17.00, 17.00, '2025-11-05 09:00:00', '2025-11-05 09:00:00');

-- Offer 5 (Atelier Schönberg, COMPLETED) — CHF 410
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (11, 5, NULL, 'Wollfilz royalblau 1.2mm, 100×180cm', 2, 165.00, 330.00, '2026-02-18 10:30:00', '2026-02-18 10:30:00');
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (12, 5, 6,    'Täppali 19-21 / S royalblau', 2, 50.00, 100.00, '2026-02-18 10:30:00', '2026-02-18 10:30:00');

-- Offer 6 (Atelier Schönberg, INVOICE) — CHF 490
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (13, 6, NULL, 'Wollfilz schwarz 1.2mm, 200×180cm', 1, 285.00, 285.00, '2026-04-22 11:00:00', '2026-04-22 11:00:00');
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (14, 6, NULL, 'Wollfilz weiss 1.2mm, 50×80cm', 1, 90.00, 90.00, '2026-04-22 11:00:00', '2026-04-22 11:00:00');
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (15, 6, NULL, 'Reststück grafitgrau, 47×55cm', 1, 115.00, 115.00, '2026-04-22 11:00:00', '2026-04-22 11:00:00');

-- Offer 7 (Textile Werkstatt Brunner, COMPLETED) — CHF 840
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (16, 7, NULL, 'Wollfilz schwarz 1.2mm, 200×180cm', 2, 285.00, 570.00, '2025-04-08 08:45:00', '2025-04-08 08:45:00');
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (17, 7, NULL, 'Wollfilz hellbeige-meliert 1.2mm, 100×180cm', 1, 160.00, 160.00, '2025-04-08 08:45:00', '2025-04-08 08:45:00');
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (18, 7, 5,    'Shopper lemon', 1, 196.00, 196.00, '2025-04-08 08:45:00', '2025-04-08 08:45:00');
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (19, 7, NULL, 'Zuschnittgebühr', 1, 24.00, 24.00, '2025-04-08 08:45:00', '2025-04-08 08:45:00');

-- Offer 8 (Textile Werkstatt Brunner, COMPLETED) — CHF 560
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (20, 8, NULL, 'Wollfilz royalblau 1.2mm, 200×180cm', 1, 310.00, 310.00, '2025-07-14 09:30:00', '2025-07-14 09:30:00');
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (21, 8, NULL, 'Wollfilz türkis 1.2mm, 100×180cm', 1, 170.00, 170.00, '2025-07-14 09:30:00', '2025-07-14 09:30:00');
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (22, 8, NULL, 'Reststück pink, 62×48cm', 1, 80.00, 80.00, '2025-07-14 09:30:00', '2025-07-14 09:30:00');

-- Offer 9 (Textile Werkstatt Brunner, COMPLETED) — CHF 720
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (23, 9, NULL, 'Wollfilz dunkelbraun-meliert 1.2mm, 200×180cm', 1, 290.00, 290.00, '2025-10-22 10:15:00', '2025-10-22 10:15:00');
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (24, 9, 1,    'Shopper weiss/schwarz', 2, 169.00, 338.00, '2025-10-22 10:15:00', '2025-10-22 10:15:00');
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (25, 9, NULL, 'Wollfilz camel-meliert 1.2mm, 50×80cm', 1, 72.00, 72.00, '2025-10-22 10:15:00', '2025-10-22 10:15:00');
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (26, 9, NULL, 'Zuschnittgebühr', 1, 20.00, 20.00, '2025-10-22 10:15:00', '2025-10-22 10:15:00');

-- Offer 10 (Textile Werkstatt Brunner, COMPLETED) — CHF 390
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (27, 10, NULL, 'Wollfilz anthrazit-meliert 1.2mm, 100×180cm', 1, 195.00, 195.00, '2026-01-30 08:00:00', '2026-01-30 08:00:00');
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (28, 10, NULL, 'Wollfilz rost-meliert 1.2mm, 100×180cm', 1, 195.00, 195.00, '2026-01-30 08:00:00', '2026-01-30 08:00:00');

-- Offer 11 (Textile Werkstatt Brunner, ORDER_CONFIRMATION) — CHF 630
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (29, 11, NULL, 'Wollfilz schwarz 1.2mm, 200×180cm', 1, 285.00, 285.00, '2026-05-15 11:30:00', '2026-05-15 11:30:00');
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (30, 11, 2,    'Shopper giftgrün/grau', 2, 169.00, 338.00, '2026-05-15 11:30:00', '2026-05-15 11:30:00');
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (31, 11, NULL, 'Zuschnittgebühr', 1, 7.00, 7.00, '2026-05-15 11:30:00', '2026-05-15 11:30:00');

-- Offer 12 (Kreativ-Markt Luzern, COMPLETED) — CHF 460
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (32, 12, NULL, 'Wollfilz weiss 1.2mm, 200×180cm', 1, 280.00, 280.00, '2025-05-14 09:00:00', '2025-05-14 09:00:00');
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (33, 12, NULL, 'Wollfilz gelb-meliert 1.2mm, 100×180cm', 1, 180.00, 180.00, '2025-05-14 09:00:00', '2025-05-14 09:00:00');

-- Offer 13 (Kreativ-Markt Luzern, COMPLETED) — CHF 310
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (34, 13, NULL, 'Reststück bordeaux, 55×70cm', 1, 150.00, 150.00, '2025-09-03 10:30:00', '2025-09-03 10:30:00');
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (35, 13, 6,    'Täppali 19-21 / S royalblau', 2, 50.00, 100.00, '2025-09-03 10:30:00', '2025-09-03 10:30:00');

-- Offer 14 (Kreativ-Markt Luzern, COMPLETED) — CHF 580
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (36, 14, NULL, 'Wollfilz blau 1.2mm, 200×180cm', 1, 315.00, 315.00, '2026-01-17 08:15:00', '2026-01-17 08:15:00');
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (37, 14, NULL, 'Wollfilz lila 1.2mm, 100×180cm', 1, 185.00, 185.00, '2026-01-17 08:15:00', '2026-01-17 08:15:00');
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (38, 14, NULL, 'Reststück dunkelrot, 42×38cm', 1, 80.00, 80.00, '2026-01-17 08:15:00', '2026-01-17 08:15:00');

-- Offer 15 (Kreativ-Markt Luzern, PAYMENT_REMINDER overdue) — CHF 420
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (39, 15, NULL, 'Wollfilz olive 1.2mm, 100×180cm', 1, 245.00, 245.00, '2026-02-28 09:00:00', '2026-02-28 09:00:00');
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (40, 15, NULL, 'Reststück pistazie, 60×55cm', 1, 175.00, 175.00, '2026-02-28 09:00:00', '2026-02-28 09:00:00');

-- Offer 16 (Handwerk & Design Huber, COMPLETED) — CHF 350
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (41, 16, NULL, 'Wollfilz dunkelbraun 1.2mm, 100×180cm', 1, 185.00, 185.00, '2025-06-19 10:00:00', '2025-06-19 10:00:00');
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (42, 16, NULL, 'Wollfilz orange 1.2mm, 50×80cm', 2, 82.50, 165.00, '2025-06-19 10:00:00', '2025-06-19 10:00:00');

-- Offer 17 (Handwerk & Design Huber, COMPLETED) — CHF 490
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (43, 17, NULL, 'Wollfilz grafitgrau 1.2mm, 200×180cm', 1, 280.00, 280.00, '2025-10-08 09:15:00', '2025-10-08 09:15:00');
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (44, 17, NULL, 'Reststück schwarz, 50×65cm', 1, 130.00, 130.00, '2025-10-08 09:15:00', '2025-10-08 09:15:00');
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (45, 17, NULL, 'Zuschnittgebühr', 1, 20.00, 20.00, '2025-10-08 09:15:00', '2025-10-08 09:15:00');

-- Offer 18 (Handwerk & Design Huber, COMPLETED) — CHF 275
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (46, 18, NULL, 'Reststück tannengrün, 65×70cm', 1, 175.00, 175.00, '2026-01-25 10:30:00', '2026-01-25 10:30:00');
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (47, 18, NULL, 'Reststück hellgrau-meliert, 48×52cm', 1, 100.00, 100.00, '2026-01-25 10:30:00', '2026-01-25 10:30:00');

-- Offer 19 (Handwerk & Design Huber, FIRST_DUNNING_NOTICE overdue) — CHF 680
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (48, 19, NULL, 'Wollfilz dunkelgrau-meliert 1.2mm, 200×180cm', 1, 295.00, 295.00, '2025-12-10 08:00:00', '2025-12-10 08:00:00');
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (49, 19, 3,    'Shopper petrol/anthrazit', 2, 169.00, 338.00, '2025-12-10 08:00:00', '2025-12-10 08:00:00');
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (50, 19, NULL, 'Zuschnittgebühr', 1, 47.00, 47.00, '2025-12-10 08:00:00', '2025-12-10 08:00:00');

-- Offer 20 (Filzkunst Atelier Müller, COMPLETED) — CHF 390
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (51, 20, NULL, 'Wollfilz violett 1.2mm, 200×180cm', 1, 265.00, 265.00, '2025-05-30 09:30:00', '2025-05-30 09:30:00');
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (52, 20, NULL, 'Reststück bordeaux, 50×58cm', 1, 125.00, 125.00, '2025-05-30 09:30:00', '2025-05-30 09:30:00');

-- Offer 21 (Filzkunst Atelier Müller, COMPLETED) — CHF 540
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (53, 21, NULL, 'Wollfilz türkis 1.2mm, 200×180cm', 1, 305.00, 305.00, '2025-09-18 10:00:00', '2025-09-18 10:00:00');
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (54, 21, NULL, 'Wollfilz rosa 1.2mm, 50×80cm', 1, 95.00, 95.00, '2025-09-18 10:00:00', '2025-09-18 10:00:00');
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (55, 21, NULL, 'Reststück pink, 44×60cm', 1, 140.00, 140.00, '2025-09-18 10:00:00', '2025-09-18 10:00:00');

-- Offer 22 (Filzkunst Atelier Müller, COMPLETED) — CHF 320
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (56, 22, NULL, 'Wollfilz apfelgrün 1.2mm, 100×180cm', 1, 175.00, 175.00, '2026-02-05 08:45:00', '2026-02-05 08:45:00');
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (57, 22, NULL, 'Reststück zitronengelb, 55×50cm', 1, 145.00, 145.00, '2026-02-05 08:45:00', '2026-02-05 08:45:00');

-- Offer 23 (Filzkunst Atelier Müller, OFFER) — CHF 450
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (58, 23, NULL, 'Wollfilz schwarz 1.2mm, 200×180cm', 1, 285.00, 285.00, '2026-05-28 11:00:00', '2026-05-28 11:00:00');
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (59, 23, NULL, 'Reststück hellgrau-meliert, 52×68cm', 1, 165.00, 165.00, '2026-05-28 11:00:00', '2026-05-28 11:00:00');

-- Offer 24 (Bastelbedarf Weber, COMPLETED) — CHF 260
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (60, 24, NULL, 'Wollfilz weiss 1.2mm, 100×180cm', 1, 160.00, 160.00, '2025-04-01 10:00:00', '2025-04-01 10:00:00');
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (61, 24, NULL, 'Reststück gelb, 40×55cm', 1, 100.00, 100.00, '2025-04-01 10:00:00', '2025-04-01 10:00:00');

-- Offer 25 (Bastelbedarf Weber, CANCELLED) — CHF 430
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (62, 25, NULL, 'Wollfilz dunkelrot 1.2mm, 200×180cm', 1, 255.00, 255.00, '2025-06-10 09:00:00', '2025-06-10 09:00:00');
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (63, 25, NULL, 'Reststück orange, 58×48cm', 1, 175.00, 175.00, '2025-06-10 09:00:00', '2025-06-10 09:00:00');

-- Offer 26 (Bastelbedarf Weber, CANCELLED) — CHF 380
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (64, 26, NULL, 'Wollfilz grafitgrau 1.2mm, 100×180cm', 1, 200.00, 200.00, '2025-10-15 08:30:00', '2025-10-15 08:30:00');
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (65, 26, 4,    'Shopper rot/anthrazit', 1, 169.00, 169.00, '2025-10-15 08:30:00', '2025-10-15 08:30:00');

-- Offer 27 (Bastelbedarf Weber, NO_RESPONSE) — CHF 510
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (66, 27, NULL, 'Wollfilz schwarz 1.2mm, 200×180cm', 1, 285.00, 285.00, '2025-12-05 10:15:00', '2025-12-05 10:15:00');
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (67, 27, NULL, 'Wollfilz weiss 1.2mm, 50×80cm', 1, 105.00, 105.00, '2025-12-05 10:15:00', '2025-12-05 10:15:00');
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (68, 27, NULL, 'Zuschnittgebühr', 1, 120.00, 120.00, '2025-12-05 10:15:00', '2025-12-05 10:15:00');

-- Offer 28 (Bastelbedarf Weber, NO_RESPONSE) — CHF 290
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (69, 28, NULL, 'Wollfilz orange 1.2mm, 100×180cm', 1, 175.00, 175.00, '2026-03-20 09:00:00', '2026-03-20 09:00:00');
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (70, 28, NULL, 'Reststück kirschrot, 45×60cm', 1, 115.00, 115.00, '2026-03-20 09:00:00', '2026-03-20 09:00:00');

-- Offer 29 (Hobby-Treff Schneider, CANCELLED) — CHF 320
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (71, 29, NULL, 'Wollfilz lila 1.2mm, 100×180cm', 1, 185.00, 185.00, '2025-07-22 09:30:00', '2025-07-22 09:30:00');
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (72, 29, NULL, 'Reststück aubergin, 48×55cm', 1, 135.00, 135.00, '2025-07-22 09:30:00', '2025-07-22 09:30:00');

-- Offer 30 (Hobby-Treff Schneider, CANCELLED) — CHF 480
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (73, 30, NULL, 'Wollfilz tannengrün 1.2mm, 200×180cm', 1, 270.00, 270.00, '2025-11-11 10:00:00', '2025-11-11 10:00:00');
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (74, 30, NULL, 'Wollfilz olive 1.2mm, 50×80cm', 1, 90.00, 90.00, '2025-11-11 10:00:00', '2025-11-11 10:00:00');
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (75, 30, 5,    'Shopper lemon', 1, 120.00, 120.00, '2025-11-11 10:00:00', '2025-11-11 10:00:00');

-- Offer 31 (Hobby-Treff Schneider, NO_RESPONSE) — CHF 390
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (76, 31, NULL, 'Wollfilz dunkelgrau-meliert 1.2mm, 200×180cm', 1, 270.00, 270.00, '2026-01-08 08:15:00', '2026-01-08 08:15:00');
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (77, 31, NULL, 'Reststück schwarz, 58×62cm', 1, 120.00, 120.00, '2026-01-08 08:15:00', '2026-01-08 08:15:00');

-- Offer 32 (Hobby-Treff Schneider, NO_RESPONSE) — CHF 260
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (78, 32, NULL, 'Wollfilz royalblau 1.2mm, 100×180cm', 1, 175.00, 175.00, '2026-04-03 11:00:00', '2026-04-03 11:00:00');
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (79, 32, NULL, 'Reststück hellblau, 40×50cm', 1, 85.00, 85.00, '2026-04-03 11:00:00', '2026-04-03 11:00:00');

-- Offer 33 (Kunsthandwerk Bachmann, COMPLETED) — CHF 430
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (80, 33, NULL, 'Wollfilz anthrazit-meliert 1.2mm, 200×180cm', 1, 290.00, 290.00, '2025-08-25 09:00:00', '2025-08-25 09:00:00');
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (81, 33, NULL, 'Reststück bordeaux, 52×68cm', 1, 140.00, 140.00, '2025-08-25 09:00:00', '2025-08-25 09:00:00');

-- Offer 34 (Kunsthandwerk Bachmann, COMPLETED) — CHF 350
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (82, 34, NULL, 'Wollfilz dunkelbraun 1.2mm, 100×180cm', 1, 195.00, 195.00, '2025-12-15 10:30:00', '2025-12-15 10:30:00');
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (83, 34, NULL, 'Wollfilz kirschrot 1.2mm, 50×80cm', 1, 155.00, 155.00, '2025-12-15 10:30:00', '2025-12-15 10:30:00');

-- Offer 35 (Kunsthandwerk Bachmann, PAYMENT_REMINDER overdue) — CHF 560
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (84, 35, NULL, 'Wollfilz schwarz 1.2mm, 200×180cm', 1, 285.00, 285.00, '2026-03-05 08:45:00', '2026-03-05 08:45:00');
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (85, 35, NULL, 'Wollfilz grafitgrau 1.2mm, 50×80cm', 1, 95.00, 95.00, '2026-03-05 08:45:00', '2026-03-05 08:45:00');
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (86, 35, NULL, 'Reststück dunkelgrau-meliert, 48×65cm', 1, 180.00, 180.00, '2026-03-05 08:45:00', '2026-03-05 08:45:00');

-- Offer 36 (Mode Atelier Keller, OFFER) — CHF 340
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (87, 36, NULL, 'Wollfilz weiss 1.2mm, 100×180cm', 1, 170.00, 170.00, '2026-05-20 10:00:00', '2026-05-20 10:00:00');
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (88, 36, NULL, 'Reststück rohweiss, 60×55cm', 1, 170.00, 170.00, '2026-05-20 10:00:00', '2026-05-20 10:00:00');

-- Offer 37 (Mode Atelier Keller, ORDER_CONFIRMATION) — CHF 715
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (89, 37, NULL, 'Wollfilz schwarz 1.2mm, 200×180cm', 1, 285.00, 285.00, '2026-04-28 09:15:00', '2026-04-28 09:15:00');
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (90, 37, 1,    'Shopper weiss/schwarz', 2, 169.00, 338.00, '2026-04-28 09:15:00', '2026-04-28 09:15:00');
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (91, 37, NULL, 'Wollfilz dunkelgrau-meliert 1.2mm, 50×80cm', 1, 72.00, 72.00, '2026-04-28 09:15:00', '2026-04-28 09:15:00');
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (92, 37, NULL, 'Zuschnittgebühr', 1, 20.00, 20.00, '2026-04-28 09:15:00', '2026-04-28 09:15:00');

-- Offer 38 (Stoff & Faden Huber, COMPLETED) — CHF 480
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (93, 38, NULL, 'Wollfilz hellgrau-meliert 1.2mm, 200×180cm', 1, 265.00, 265.00, '2025-07-30 08:30:00', '2025-07-30 08:30:00');
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (94, 38, NULL, 'Wollfilz weiss 1.2mm, 50×80cm', 1, 95.00, 95.00, '2025-07-30 08:30:00', '2025-07-30 08:30:00');
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (95, 38, NULL, 'Reststück rohweiss, 44×58cm', 1, 120.00, 120.00, '2025-07-30 08:30:00', '2025-07-30 08:30:00');

-- Offer 39 (Stoff & Faden Huber, COMPLETED) — CHF 370
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (96, 39, NULL, 'Wollfilz dunkelbraun-meliert 1.2mm, 100×180cm', 1, 210.00, 210.00, '2025-11-20 10:00:00', '2025-11-20 10:00:00');
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (97, 39, NULL, 'Wollfilz camel-meliert 1.2mm, 100×180cm', 1, 160.00, 160.00, '2025-11-20 10:00:00', '2025-11-20 10:00:00');

-- Offer 40 (Stoff & Faden Huber, SECOND_DUNNING_NOTICE — serious overdue) — CHF 890
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (98,  40, NULL, 'Wollfilz schwarz 1.2mm, 200×180cm', 2, 285.00, 570.00, '2025-12-20 09:00:00', '2025-12-20 09:00:00');
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (99,  40, 2,    'Shopper giftgrün/grau', 1, 169.00, 169.00, '2025-12-20 09:00:00', '2025-12-20 09:00:00');
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (100, 40, NULL, 'Reststück anthrazit-meliert, 60×70cm', 1, 151.00, 151.00, '2025-12-20 09:00:00', '2025-12-20 09:00:00');
INSERT IGNORE INTO offer_item (id, offer_id, product_variant_id, description, quantity, unit_price, total_price, created_at, updated_at)
VALUES (101, 40, NULL, 'Mahngebühr', 1, 0.00, 0.00, '2025-12-20 09:00:00', '2025-12-20 09:00:00');
