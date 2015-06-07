-- Ivan Aguirre
-- CS166 Project
-- Triggers and Stored Procedures
-- June 6, 2015
DROP SEQUENCE message_id_seq;
DROP FUNCTION incr_message_num();
DROP LANGUAGE plpgsql;

CREATE SEQUENCE message_id_seq START WITH 50000;

CREATE LANGUAGE plpgsql;
CREATE OR REPLACE FUNCTION incr_message_num()
RETURNS "trigger" AS
$BODY$
  BEGIN
    NEW.msgId := nextval('message_id_seq');
    NEW.sendTime := current_timestamp;
    NEW.deleteStatus := 0;
    NEW.status := 'Sent';
    return NEW;
  END;
$BODY$
LANGUAGE plpgsql VOLATILE;

CREATE TRIGGER name BEFORE INSERT
ON message FOR EACH ROW 
EXECUTE PROCEDURE incr_message_num();
