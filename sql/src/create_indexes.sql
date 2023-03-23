CREATE INDEX IF NOT EXISTS hotelid_managerid_idx ON Hotel (hotelID, managerUserID);


/*index for RoomRepairs*/
CREATE INDEX IF NOT EXISTS repairID_idx ON RoomRepairs (repairID);
CREATE INDEX if NOT EXISTS companyID_idx ON RoomRepairs  (companyID);
CREATE INDEX if NOT EXISTS hotelID_idx ON RoomRepairs  (hotelID);
CREATE INDEX if NOT EXISTS roomNumber_idx ON RoomRepairs (roomNumber);
CREATE INDEX if NOT EXISTS repairDate_idx ON RoomRepairs (repairDate);


