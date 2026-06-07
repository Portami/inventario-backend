-- Reference data the application needs in every environment: felt types, suppliers and storage
-- locations. Demo/sample inventory and offer data is seeded separately and only in the dev profile.

INSERT INTO felt_type (id, name) VALUES
    (1, 'Wollfilz'),
    (2, 'Reinwollfilz'),
    (3, 'Visco-Wollfilz'),
    (4, 'Universalfilz');

INSERT INTO supplier (id, name) VALUES
    (1, 'Wiler'),
    (2, 'Birki'),
    (3, 'M&K'),
    (4, 'VFG'),
    (5, 'Holland'),
    (6, 'Schafwoll');

INSERT INTO storage (id, name) VALUES
    (1, 'Atelier'),
    (2, 'Keller');
