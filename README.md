# USER STORY #1 (Sara)

Come Sara vorrei registrarmi in piattaforma per inserire un articolo, in modo tale da lavorare per The Aulab Chronicle.

ACCEPTANCE CRITERIA:
- Utente deve potersi registrare/loggare in piattaforma
- Bottone “inserisci articolo” in home solo a utenti loggati
- Articolo composto da:
- Titolo
- Sottotitolo
- Corpo del Testo
- Categorie dell'annuncio pre-compilate
- La relazione tra Categoria e Annuncio è 1-a-N
- La relazione tra Utente e Annuncio è 1-a-N
- Ad annuncio inserito visualizzare un messaggio di conferma


# USER STORY #2 (Lorenzo)

Come Lorenzo vorrei visualizzare gli ultimi articoli sul portale in modo tale da informarmi su ciò che succede nel mondo.

ACCEPTANCE CRITERIA:
- Manipolazione delle immagini con supabase
- Nella home, solo gli articoli più recenti (scegliete voi numero)
- In home e index, ordine dal più recente al più vecchio
- Pagina dettaglio per ogni articolo
- Click per ricerca per categoria
- Click per ricerca per scrittore


# USER STORY #3 (Marta)

Come Marta vorrei poter contare su una funzione di fact-checking in modo tale da poter controllare quali notizie pubblicare.

ACCEPTANCE CRITERIA:
- Tre nuovi ruoli: Admin, Revisor, Writer
- Un utente registrato richiede di entrare a far parte del team tramite un form di "lavora con noi"
- Creazione di una dashboard per il proprietario della piattaforma per poter gestire le richieste
- Permettere solo all'admin la modifi ca e cancellazione delle categorie
- Gli utenti revisori avranno una sezione a loro dedicata con tutti gli articoli da revisionare
- Bottone accetta articolo
- Bottone rifi uta articolo


# USER STORY #4 (Lorenzo)

Come Lorenzo vorrei poter cercare tra gli articoli in modo tale da visualizzare subito quello che mi interessa.

ACCEPTANCE CRITERIA:
- Implementazione della ricerca full-text
- Ricerca per titolo
- Ricerca per sottotitolo
- Ricerca per categoria

# USER STORY #5 (Sara)

Come Sara vorrei poter cancellare o modifi care gli articoli che ho scritto in modo tale da consegnare sempre un lavoro d'alta qualità.

ACCEPTANCE CRITERIA:
- Creazione di una dashboard dedicata ai writer
- Permettere solo al writer specifico la modifica di un articolo
- Permettere solo al writer specifico la cancellazione di un articolo

EXTRA:
- Se l'articolo viene modificato, deve ritornare alla revisione