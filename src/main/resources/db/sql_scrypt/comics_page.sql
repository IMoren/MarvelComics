-- Table: public.comics_page

-- DROP TABLE public.comics_page;

CREATE TABLE public.comics_page
(
  comics_id int8 NOT NULL,
  order_page int8,
  path_file character varying(255),
  CONSTRAINT comics_page_pkey PRIMARY KEY (path_file),
  CONSTRAINT comics_page_comics_id_fkey FOREIGN KEY (comics_id)
      REFERENCES public.comics (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.comics_page
  OWNER TO postgres;
