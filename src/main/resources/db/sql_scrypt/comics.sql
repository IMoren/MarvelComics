-- Table: public.comics

-- DROP TABLE public.comics;
CREATE SEQUENCE public.com_id_seq
START WITH 1
INCREMENT BY 1
NO MINVALUE
NO MAXVALUE
CACHE 1;

CREATE TABLE public.comics
(
  id int8 NOT NULL,
  cover CHARACTER VARYING255),
  release CHARACTER VARYING(255),
  title CHARACTER VARYING(255),
  CONSTRAINT comics_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE ONLY public.comics
	ALTER COLUMN id 
	SET DEFAULT nextval('public.com_id_seq'::regclass);

ALTER TABLE public.comics
  OWNER TO postgres;
