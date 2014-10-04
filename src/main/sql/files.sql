CREATE TABLE files
(
  id numeric NOT NULL,
  nv integer NOT NULL,
  dv integer NOT NULL,
  sid numeric NOT NULL,
  snv integer NOT NULL,
  sdv integer NOT NULL,
  level integer NOT NULL,
  name character varying NOT NULL,
  content oid NOT NULL,
  CONSTRAINT files_pkey PRIMARY KEY (id)
);

CREATE INDEX files_name_idx ON files (name);
CREATE INDEX files_level_idx ON files (level);