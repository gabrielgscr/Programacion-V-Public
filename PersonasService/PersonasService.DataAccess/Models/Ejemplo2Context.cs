using System;
using System.Collections.Generic;
using Microsoft.EntityFrameworkCore;

namespace PersonasService.DataAccess.Models;

public partial class Ejemplo2Context : DbContext
{
    public Ejemplo2Context()
    {
    }

    public Ejemplo2Context(DbContextOptions<Ejemplo2Context> options)
        : base(options)
    {
    }

    public virtual DbSet<Persona> Persona { get; set; }

    public virtual DbSet<Rol> Rol { get; set; }

    public virtual DbSet<Telefono> Telefono { get; set; }

    protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
        => optionsBuilder.UseSqlServer("Name=DefaultConnection");
        //=> optionsBuilder.UseSqlServer("Server=(local);Database=Ejemplo2;User id=sa; password=Holamundo1!;Encrypt=False;");

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<Persona>(entity =>
        {
            entity.Property(e => e.PersonaId)
                .HasMaxLength(50)
                .IsUnicode(false)
                .HasColumnName("PersonaID");
            entity.Property(e => e.Gender)
                .HasMaxLength(10)
                .IsUnicode(false)
                .HasDefaultValue("");
            entity.Property(e => e.Nombre)
                .HasMaxLength(50)
                .IsUnicode(false);
            entity.Property(e => e.Password)
                .HasMaxLength(100)
                .IsUnicode(false)
                .HasDefaultValue("");

            entity.HasMany(d => d.Rol).WithMany(p => p.Persona)
                .UsingEntity<Dictionary<string, object>>(
                    "PersonaRol",
                    r => r.HasOne<Rol>().WithMany()
                        .HasForeignKey("RolId")
                        .OnDelete(DeleteBehavior.ClientSetNull)
                        .HasConstraintName("FK_PersonaRol_Rol"),
                    l => l.HasOne<Persona>().WithMany()
                        .HasForeignKey("PersonaId")
                        .OnDelete(DeleteBehavior.ClientSetNull)
                        .HasConstraintName("FK_PersonaRol_Persona"),
                    j =>
                    {
                        j.HasKey("PersonaId", "RolId");
                        j.IndexerProperty<string>("PersonaId")
                            .HasMaxLength(50)
                            .IsUnicode(false)
                            .HasColumnName("PersonaID");
                        j.IndexerProperty<int>("RolId").HasColumnName("RolID");
                    });
        });

        modelBuilder.Entity<Rol>(entity =>
        {
            entity.Property(e => e.RolId).HasColumnName("RolID");
            entity.Property(e => e.Nombre)
                .HasMaxLength(50)
                .IsUnicode(false);
        });

        modelBuilder.Entity<Telefono>(entity =>
        {
            entity.Property(e => e.TelefonoId).HasColumnName("TelefonoID");
            entity.Property(e => e.PersonaId)
                .HasMaxLength(50)
                .IsUnicode(false)
                .HasColumnName("PersonaID");
            entity.Property(e => e.Telefono1)
                .HasMaxLength(15)
                .IsUnicode(false)
                .HasColumnName("Telefono");

            entity.HasOne(d => d.Persona).WithMany(p => p.Telefono)
                .HasForeignKey(d => d.PersonaId)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("FK_Telefono_Persona");
        });

        OnModelCreatingPartial(modelBuilder);
    }

    partial void OnModelCreatingPartial(ModelBuilder modelBuilder);
}
