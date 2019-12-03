-- Table: public.characters

-- DROP TABLE public.characters;
CREATE SEQUENCE public.char_id_seq
START WITH 1
INCREMENT BY 1
NO MINVALUE
NO MAXVALUE
CACHE 1;

CREATE TABLE public.characters
(
  id int8 NOT NULL,
  create_date CHARACTER VARYING(255),
  description CHARACTER VARYING(2000),
  name CHARACTER VARYING(255),
  portrait CHARACTER VARYING(255),
  biography CHARACTER VARYING(10000),
  CONSTRAINT characters_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE ONLY public.characters
    ALTER COLUMN id 
        SET DEFAULT NEXTVAL('public.char_id_seq'::regclass);
ALTER TABLE public.characters
  OWNER TO postgres;
