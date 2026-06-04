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
