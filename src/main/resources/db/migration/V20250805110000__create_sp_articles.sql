DELIMITER $$
CREATE PROCEDURE update_stock(
    IN productId INT,
    IN quantity INT
)
BEGIN
    UPDATE articulos
    SET stock = stock - quantity
    WHERE id = productId;
END$$
DELIMITER ;