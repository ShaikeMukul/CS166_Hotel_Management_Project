DROP FUNCTION IF EXISTS insert_roomrepair_to_request();
CREATE OR REPLACE LANGUAGE plpgsql;
CREATE OR REPLACE FUNCTION insert_roomrepair_to_request()
RETURNS TRIGGER AS $BODY$
DECLARE
	manager_ID integer;
        hotel_ID integer;   
BEGIN
        SELECT hotelID INTO hotel_ID FROM RoomRepairs Where NEW.repairID = repairID;
	SELECT managerUserID INTO manager_ID FROM Hotel WHERE hotel_ID = hotelID;
	INSERT INTO RoomRepairRequests (managerID, repairID) Values (manager_ID, NEW.repairID); 
	RETURN NEW;
END;
$BODY$ LANGUAGE plpgsql VOLATILE;

DROP TRIGGER IF EXISTS insert_roomrepair_to_request ON RoomRepairs;
CREATE TRIGGER insert_roomrepair_to_request
AFTER INSERT ON RoomRepairs
FOR EACH ROW
EXECUTE PROCEDURE insert_roomrepair_to_request();
  
