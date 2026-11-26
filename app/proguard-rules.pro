# LUSTRUM: WorkManager creează CleanWorker prin reflecție; nu trebuie redenumit sau eliminat.
-keep class tf.trom.lustrum.CleanWorker { <init>(...); }
