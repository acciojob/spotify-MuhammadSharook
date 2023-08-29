package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository() {
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {
        User user = new User(name, mobile);
        users.add(user);
        return user;
    }

    public Artist createArtist(String name) {
        Artist artist = new Artist(name);
        artists.add(artist);
        return artist;
    }

    public Album createAlbum(String title, String artistName) {
        Artist a = null;
        for (Artist artist : artists) {
            if (artist.getName().equals(artistName)) {
                a = artist;
                break;
            }
        }

        if (a == null) {
            a = createArtist(artistName);
            Album album = new Album(title);
            albums.add(album);
            List<Album> l = new ArrayList<>();
            l.add(album);
            artistAlbumMap.put(a, l);
            return album;
        } else {
            Album album = new Album(title);
            albums.add(album);
            List<Album> l = new ArrayList<>();
            l.add(album);
            artistAlbumMap.put(a, l);
            return album;
        }
    }

    public Song createSong(String title, String albumName, int length) throws Exception {
        Album a = null;
        for (Album album : albums) {
            if (album.getTitle().equals(albumName)) {
                a = album;
                break;
            }
        }
        if (a == null) {
            throw new Exception("Album does not exist");
        } else {
            Song song = new Song(title, length);
            song.setLikes(0);
            songs.add(song);
            if (albumSongMap.containsKey(a)) {
                List<Song> l = albumSongMap.get(a);
                l.add(song);
                albumSongMap.put(a, l);
            } else {
                List<Song> l = new ArrayList<>();
                l.add(song);
                albumSongMap.put(a, l);
            }
            return song;
        }
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        User u = null;
        for (User user : users) {
            if (user.getMobile().equals(mobile)) {
                u = user;
                break;
            }
        }
        if (u == null) {
            throw new Exception("User does not exist");
        } else {
            Playlist playlist = new Playlist(title);
            playlists.add(playlist);
            List<Song> l = new ArrayList<>();
            for (Song song : songs) {
                if (song.getLength() == length) {
                    l.add(song);
                }
            }
            playlistSongMap.put(playlist, l);
            List<User> l1 = new ArrayList<>();
            l1.add(u);
            playlistListenerMap.put(playlist, l1);
            creatorPlaylistMap.put(u, playlist);

            if (userPlaylistMap.containsKey(u)) {
                List<Playlist> l2 = userPlaylistMap.get(u);
                l2.add(playlist);
                userPlaylistMap.put(u, l2);
            } else {
                List<Playlist> l2 = new ArrayList<>();
                l2.add(playlist);
                userPlaylistMap.put(u, l2);
            }
            return playlist;
        }
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        User u = null;
        for (User user : users) {
            if (user.getMobile().equals(mobile)) {
                u = user;
                break;
            }
        }
        if (u == null) {
            throw new Exception("User does not exist");
        } else {
            Playlist playlist = new Playlist(title);
            playlists.add(playlist);
            List<Song> l = new ArrayList<>();
            for (Song song : songs) {
                if (song.getTitle().equals(title)) {
                    l.add(song);
                }
            }
            playlistSongMap.put(playlist, l);
            List<User> l1 = new ArrayList<>();
            l1.add(u);
            playlistListenerMap.put(playlist, l1);
            creatorPlaylistMap.put(u, playlist);
            if (userPlaylistMap.containsKey(u)) {
                List<Playlist> l2 = userPlaylistMap.get(u);
                l2.add(playlist);
                userPlaylistMap.put(u, l2);
            } else {
                List<Playlist> l2 = new ArrayList<>();
                l2.add(playlist);
                userPlaylistMap.put(u, l2);
            }
            return playlist;
        }
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        User u = null;
        for (User user : users) {
            if (user.getMobile().equals(mobile)) {
                u = user;
                break;
            }
        }
        if (u == null) {
            throw new Exception("User does not exist");
        }
        Playlist p = null;
        for (Playlist playlist : playlists) {
            if (playlist.getTitle().equals(playlistTitle)) {
                p = playlist;
                break;
            }
        }
        if (p == null) {
            throw new Exception("Playlist does not exist");
        }
        if (creatorPlaylistMap.containsKey(u)) {
            return p;
        }
        List<User> l = playlistListenerMap.get(p);
        for (User user : l) {
            if (user == u) {
                return p;
            }
        }
        l.add(u);
        playlistListenerMap.put(p, l);
        List<Playlist> l2 = userPlaylistMap.get(u);
        if (l2 == null) {
            l2 = new ArrayList<>();
        }
        l2.add(p);
        userPlaylistMap.put(u, l2);
        return p;
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        User u = null;
        for (User user : users) {
            if (user.getMobile().equals(mobile)) {
                u = user;
                break;
            }
        }
        if (u == null)
            throw new Exception("User does not exist");

        Song s = null;
        for (Song song : songs) {
            if (song.getTitle().equals(songTitle)) {
                s = song;
                break;
            }
        }
        if (s == null)
            throw new Exception("Song does not exist");

        if (songLikeMap.containsKey(s)) {
            List<User> l = songLikeMap.get(s);
            if (l.contains(u)) {
                return s;
            } else {
                int likes = s.getLikes() + 1;
                s.setLikes(likes);
                l.add(u);
                songLikeMap.put(s, l);

                Album a = null;
                for (Album album : albumSongMap.keySet()) {
                    List<Song> songList = albumSongMap.get(a);
                    if (songList.contains(s)) {
                        a = album;
                        break;
                    }
                }
                Artist artist = null;
                for (Artist x : artistAlbumMap.keySet()) {
                    List<Album> albumList = artistAlbumMap.get(x);
                    if (albumList.contains(a)) {
                        artist = x;
                        break;
                    }
                }
                int likes1 = artist.getLikes() + 1;
                artist.setLikes(likes1);
                artists.add(artist);
                return s;
            }
        } else {
            int likes = s.getLikes() + 1;
            s.setLikes(likes);
            List<User> l2 = new ArrayList<>();
            l2.add(u);
            songLikeMap.put(s, l2);

            Album a = null;
            for (Album album : albumSongMap.keySet()) {
                List<Song> songList = albumSongMap.get(album);
                if (songList.contains(s)) {
                    a = album;
                    break;
                }
            }
            Artist x = null;
            for (Artist artist : artistAlbumMap.keySet()) {
                List<Album> albumList = artistAlbumMap.get(artist);
                if (albumList.contains(a)) {
                    x = artist;
                    break;
                }
            }
            int likes1 = x.getLikes() + 1;
            x.setLikes(likes1);
            artists.add(x);

            return s;
        }
    }

    public String mostPopularArtist() {
        int max_likes = 0;
        Artist a = null;

        for (Artist artist : artists) {
            if (artist.getLikes() >= max_likes) {
                a = artist;
                max_likes = artist.getLikes();
            }
        }
        if (a == null) {
            return null;
        } else {
            return a.getName();
        }
    }

    public String mostPopularSong() {
        int max = 0;
        Song song = null;

        for (Song s : songLikeMap.keySet()) {
            if (s.getLikes() >= max) {
                song = s;
                max = s.getLikes();
            }
        }
        if (song == null)
            return null;
        else
            return song.getTitle();
    }
}

