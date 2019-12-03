-- Table: public.comics_has_character

-- DROP TABLE public.comics_has_character;

CREATE TABLE public.comics_has_character
(
  comics_id int8 NOT NULL,
  character_id int8 NOT NULL,
  UNIQUE (comics_id, character_id),
  CONSTRAINT comics_has_character_caracter_id_fkey FOREIGN KEY (character_id)
      REFERENCES public.characters (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT comics_has_character_comics_id_fkey FOREIGN KEY (comics_id)
      REFERENCES public.comics (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.comics_has_character
  OWNER TO postgres;
