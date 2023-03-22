CREATE OR REPLACE LANGUAGE plpgsql;
CREATE OR REPLACE FUNCTION roomRepair_to_Request()
RETURNS TRIGGER AS $$
BEGIN
	UPDATE roomRepairRequests
	SET some_column = New.some_column
	Where id = NEW.id
RETURN new;
END;
$BODY$
LANGUAGE plpgsql VOLATILE;

CREATE TRIGGER roomRepair_to_request_trigger 
AFTER INSERT ON RoomRepairs
For EACH ROW
EXECUTE PROCEDURE generate_room_repair_request();
  
