INSERT INTO identity_info(id, created_at)
SELECT 1, TIMESTAMP '2019-03-03 12:13:14'
WHERE NOT EXISTS (SELECT 1 FROM identity_info WHERE id = 1);

INSERT INTO product(id, name, price, currency_code)
SELECT 1, 'A shoe with shoelaces', 1.01, 'EUR'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE id = 1);

INSERT INTO identity_info(id, created_at)
SELECT 2, TIMESTAMP '2019-05-05 00:00:00'
WHERE NOT EXISTS (SELECT 1 FROM identity_info WHERE id = 2);

INSERT INTO product(id, name, price, currency_code)
SELECT 2, 'A book on how to win at life', 2.02, 'EUR'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE id = 2);

INSERT INTO identity_info(id, created_at)
SELECT 3, TIMESTAMP '2019-07-07 23:59:59'
WHERE NOT EXISTS (SELECT 1 FROM identity_info WHERE id = 3);

INSERT INTO product(id, name, price, currency_code)
SELECT 3, 'Broken car', 7821.33, 'EUR'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE id = 3);
